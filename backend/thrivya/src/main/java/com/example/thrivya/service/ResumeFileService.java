package com.example.thrivya.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeFileService {

    @Value("${thrivya.upload.directory:uploads/resumes}")
    private String uploadDirectory;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    "application/pdf",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/msword"
            );

    public StoredResume saveAndExtract(
            MultipartFile file
    ) {
        validateFile(file);

        try {
            Path uploadPath = Paths.get(uploadDirectory)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadPath);

            String originalFileName =
                    file.getOriginalFilename() == null
                            ? "resume"
                            : Paths.get(
                            file.getOriginalFilename()
                    ).getFileName().toString();

            String extension =
                    getExtension(originalFileName);

            String storedFileName =
                    UUID.randomUUID() + extension;

            Path targetPath =
                    uploadPath.resolve(storedFileName)
                            .normalize();

            if (!targetPath.startsWith(uploadPath)) {
                throw new IllegalArgumentException(
                        "Invalid file path"
                );
            }

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String extractedText =
                    extractText(
                            targetPath,
                            extension
                    );

            return new StoredResume(
                    originalFileName,
                    storedFileName,
                    targetPath.toString(),
                    file.getContentType(),
                    extractedText
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to save or read resume",
                    exception
            );
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a resume file"
            );
        }

        String originalFileName =
                file.getOriginalFilename() == null
                        ? ""
                        : file.getOriginalFilename()
                        .toLowerCase(Locale.ROOT);

        boolean validExtension =
                originalFileName.endsWith(".pdf")
                        || originalFileName.endsWith(".docx")
                        || originalFileName.endsWith(".doc");

        boolean validContentType =
                file.getContentType() != null
                        && ALLOWED_CONTENT_TYPES.contains(
                        file.getContentType()
                );

        if (!validExtension || !validContentType) {
            throw new IllegalArgumentException(
                    "Only PDF or DOC/DOCX resume files are allowed"
            );
        }
    }

    private String extractText(
            Path filePath,
            String extension
    ) throws IOException {

        if (extension.equalsIgnoreCase(".pdf")) {
            try (PDDocument document =
                         Loader.loadPDF(filePath.toFile())) {

                PDFTextStripper stripper =
                        new PDFTextStripper();

                stripper.setSortByPosition(true);

                return cleanText(
                        stripper.getText(document)
                );
            }
        }

        try (
                InputStream inputStream =
                        Files.newInputStream(filePath);
                XWPFDocument document =
                        new XWPFDocument(inputStream);
                XWPFWordExtractor extractor =
                        new XWPFWordExtractor(document)
        ) {
            return cleanText(
                    extractor.getText()
            );
        }
    }

    private String cleanText(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("\u0000", "")
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");

        if (index < 0) {
            return "";
        }

        return fileName.substring(index)
                .toLowerCase(Locale.ROOT);
    }

    public record StoredResume(
            String originalFileName,
            String storedFileName,
            String filePath,
            String contentType,
            String extractedText
    ) {
    }
}