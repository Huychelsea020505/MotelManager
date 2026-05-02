import { useState } from "react";
import LoginPage from "./pages/LoginPage.jsx";
import DashboardPage from "./pages/DashboardPage.jsx";
import RoomsPage from "./pages/RoomsPage.jsx";
import TenantsPage from "./pages/TenantsPage.jsx";
import InvoicesPage from "./pages/InvoicesPage.jsx";
import PaymentsPage from "./pages/PaymentsPage.jsx";

const menuItems = [
  { key: "dashboard", label: "Dashboard", title: "Tổng quan" },
  { key: "rooms", label: "Phòng trọ", title: "Quản lý phòng trọ" },
  { key: "tenants", label: "Người thuê", title: "Quản lý người thuê" },
  { key: "invoices", label: "Hóa đơn", title: "Quản lý hóa đơn" },
  { key: "payments", label: "Thanh toán", title: "Lịch sử thanh toán" },
];

export default function App() {
  const storedUser = localStorage.getItem("motel_user");
  const [user, setUser] = useState(storedUser ? JSON.parse(storedUser) : null);
  const [activePage, setActivePage] = useState("dashboard");

  function handleLogin(data) {
    localStorage.setItem("motel_user", JSON.stringify(data));
    setUser(data);
  }

  function handleLogout() {
    localStorage.removeItem("motel_user");
    setUser(null);
    setActivePage("dashboard");
  }

  if (!user) {
    return <LoginPage onLogin={handleLogin} />;
  }

  const currentTitle = menuItems.find((item) => item.key === activePage)?.title;

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">M</div>
          <div>
            <strong>3H Motel Manager</strong>
            <span>Boarding house</span>
          </div>
        </div>

        <nav className="nav-list">
          {menuItems.map((item) => (
            <button
              key={item.key}
              className={activePage === item.key ? "nav-item active" : "nav-item"}
              onClick={() => setActivePage(item.key)}
            >
              {item.label}
            </button>
          ))}
        </nav>
      </aside>

      <main className="main-area">
        <header className="topbar">
          <div>
            <span className="kicker">Motel Management System</span>
            <h1>{currentTitle}</h1>
          </div>
          <div className="account">
            <span>{user.fullName || user.username}</span>
            <button className="ghost-button" onClick={handleLogout}>
              Đăng xuất
            </button>
          </div>
        </header>

        {activePage === "dashboard" && <DashboardPage />}
        {activePage === "rooms" && <RoomsPage />}
        {activePage === "tenants" && <TenantsPage />}
        {activePage === "invoices" && <InvoicesPage />}
        {activePage === "payments" && <PaymentsPage />}
      </main>
    </div>
  );
}
