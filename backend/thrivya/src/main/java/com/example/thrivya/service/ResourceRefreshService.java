package com.example.thrivya.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ResourceRefreshService {

    @Scheduled(
            fixedDelayString =
                    "${thrivya.resources.refresh-ms:86400000}"
    )
    public void refreshResources() {
        log.info(
                "Resource refresh task started."
        );

        /*
         * Current MVP:
         * Resources are curated manually.
         * No external API is called yet.
         *
         * Future workflow:
         * 1. Read active skills.
         * 2. Call provider APIs.
         * 3. Validate URLs.
         * 4. Remove duplicates.
         * 5. Insert or update resources.
         * 6. Mark expired resources inactive.
         */
    }
}