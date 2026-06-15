import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../api/authApi.js";

export default function LoginPage() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        username: "",
        password: "",
    });

    const [message, setMessage] = useState("");

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleLogin = async (e) => {
        e.preventDefault();

        try {
            const response = await loginUser(formData);

            console.log("Login response:", response.data);

            const authData = response.data.data;

            localStorage.setItem("token", authData.token);
            localStorage.setItem("userId", authData.userId);
            localStorage.setItem("username", authData.username);
            localStorage.setItem("role", authData.role);

            navigate("/dashboard");
        } catch (error) {
            console.error("Login error:", error);
            setMessage("Invalid username or password.");
        }
    };

    return (
        <div className="login-page">
            <div className="login-card">
                <h1>Login</h1>
                <p>Welcome back to CompassChat</p>

                <form onSubmit={handleLogin}>
                    <input
                        type="text"
                        name="username"
                        placeholder="Username"
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />

                    <input
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />

                    <button type="submit">Login</button>
                </form>

                {message && <p>{message}</p>}

                <button type="button" onClick={() => navigate("/register")}>
                    Create new account
                </button>
            </div>
        </div>
    );
}