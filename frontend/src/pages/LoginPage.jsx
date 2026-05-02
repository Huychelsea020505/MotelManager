import { useState } from "react";
import { api } from "../api";

export default function LoginPage({ onLogin }) {
  const [form, setForm] = useState({ username: "admin", password: "123456" });
  const [error, setError] = useState("");

  async function login(event) {
    event.preventDefault();
    setError("");

    try {
      const data = await api("/auth/login", {
        method: "POST",
        body: JSON.stringify(form),
      });
      onLogin(data);
    } catch (err) {
      setError("Tên đăng nhập hoặc mật khẩu không đúng.");
    }
  }

  return (
    <main className="login-screen">
      <section className="login-card">
        <div className="login-intro">
          <span className="kicker">Motel Management System</span>
          <h1>Quản lý nhà trọ</h1>
          <p>Theo dõi phòng, người thuê, hóa đơn và thanh toán trong một giao diện rõ ràng.</p>
        </div>

        <form className="login-form" onSubmit={login}>
          <h2>Đăng nhập</h2>
          {error && <div className="alert error">{error}</div>}
          <label>
            Tên đăng nhập
            <input value={form.username} onChange={(event) => setForm({ ...form, username: event.target.value })} />
          </label>
          <label>
            Mật khẩu
            <input type="password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} />
          </label>
          <button className="primary-button">Đăng nhập</button>
        </form>
      </section>
    </main>
  );
}
