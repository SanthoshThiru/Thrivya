import { useNavigate } from "react-router-dom";

export default function LogoutButton() {
  const navigate = useNavigate();

  function handleLogout() {
    localStorage.removeItem("thrivyaToken");
    localStorage.removeItem("thrivyaUser");

    navigate("/home", {
      replace: true,
    });
  }

  return (
    <button
      type="button"
      onClick={handleLogout}
      style={styles.button}
    >
      Logout
    </button>
  );
}

const styles = {
  button: {
    marginTop: "20px",
    padding: "10px 18px",
    border: "none",
    borderRadius: "8px",
    backgroundColor: "#dc2626",
    color: "#ffffff",
    fontSize: "15px",
    fontWeight: "600",
    cursor: "pointer",
  },
};