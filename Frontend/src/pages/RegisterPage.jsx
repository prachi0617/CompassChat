import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../api/authApi.js";

export default function RegisterPage() {
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

    const handleRegister = async (e) => {
        e.preventDefault();

        try {
            const response = await registerUser(formData);

            console.log("Register response:", response.data);

            const authData = response.data.data;

            localStorage.setItem("token", authData.token);
            localStorage.setItem("userId", authData.userId);
            localStorage.setItem("username", authData.username);
            localStorage.setItem("role", authData.role);

            setMessage("Registration successful!");

            navigate("/dashboard");
        } catch (error) {
            console.error("Register error:", error);
            setMessage("Registration failed. Username may already be taken.");
        }
    };

    return (
        <div className="login-page">
            <div className="login-card">
                <h1>Create Account</h1>
                <p>Join CompassChat</p>

                <form onSubmit={handleRegister}>
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

                    <button type="submit">Register</button>
                </form>

                {message && <p>{message}</p>}

                <button type="button" onClick={() => navigate("/login")}>
                    Already have an account? Login
                </button>
            </div>
        </div>
    );
}