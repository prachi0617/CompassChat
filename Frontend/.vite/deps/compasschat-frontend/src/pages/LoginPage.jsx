import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser } from "../api/authApi";

function LoginPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: "",
    password: "",
  });

  const [error, setError] = useState("");

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const data = await loginUser(form);

      if (data.token) {
        localStorage.setItem("token", data.token);
      }

      if (data.userId) {
        localStorage.setItem("userId", data.userId);
      }

      localStorage.setItem("username", form.username);

      navigate("/dashboard");
    } catch (err) {
      setError("Login failed. Please check your username and password.");
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleLogin}>
        <h1>CompassChat</h1>
        <p>Login to continue</p>

        {error && <div className="error-box">{error}</div>}

        <input
          type="text"
          name="username"
          placeholder="Username"
          value={form.username}
          onChange={handleChange}
          required
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          required
        />

        <button type="submit">Login</button>

        <span>
          New user? <Link to="/register">Create account</Link>
        </span>
      </form>
    </div>
  );
}

export default LoginPage;
