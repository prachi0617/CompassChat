import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../api/authApi";

function RegisterPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    username: "",
    email: "",
    password: "",
    userType: "COMMUNITY_MEMBER",
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    try {
      const data = await registerUser(form);

      if (data.success === false) {
        setError(data.message || "Registration failed.");
        return;
      }

      setSuccess("Account created successfully. Please login.");

      setTimeout(() => {
        navigate("/login");
      }, 800);
    } catch (err) {
      setError("Registration failed. Username or email may already exist.");
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleRegister}>
        <h1>Create Account</h1>
        <p>Join CompassChat</p>

        {error && <div className="error-box">{error}</div>}
        {success && <div className="success-box">{success}</div>}

        <input type="text" name="name" placeholder="Full name" value={form.name} onChange={handleChange} required />
        <input type="text" name="username" placeholder="Username" value={form.username} onChange={handleChange} required />
        <input type="email" name="email" placeholder="Email" value={form.email} onChange={handleChange} required />
        <input type="password" name="password" placeholder="Password" value={form.password} onChange={handleChange} required />

        <select name="userType" value={form.userType} onChange={handleChange}>
          <option value="COMMUNITY_MEMBER">Community Member</option>
          <option value="VOLUNTEER">Volunteer</option>
          <option value="FAMILY">Family</option>
          <option value="ADMIN">Admin</option>
        </select>

        <button type="submit">Register</button>

        <span>
          Already have an account? <Link to="/login">Login</Link>
        </span>
      </form>
    </div>
  );
}

export default RegisterPage;
