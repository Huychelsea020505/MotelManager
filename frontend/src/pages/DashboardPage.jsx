import { useEffect, useState } from "react";
import { api, money } from "../api";

export default function DashboardPage() {
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    loadDashboard();
  }, []);

  async function loadDashboard() {
    setError("");
    try {
      setDashboard(await api("/dashboard"));
    } catch (err) {
      setError("Không thể tải dữ liệu dashboard.");
    }
  }

  return (
    <div className="page-stack">
      {error && <div className="alert error">{error}</div>}

      <section className="stats-grid">
        <article className="stat-card blue"><span>Tổng phòng</span><strong>{dashboard?.totalRooms || 0}</strong></article>
        <article className="stat-card green"><span>Phòng trống</span><strong>{dashboard?.availableRooms || 0}</strong></article>
        <article className="stat-card amber"><span>Đang thuê</span><strong>{dashboard?.occupiedRooms || 0}</strong></article>
        <article className="stat-card red"><span>Doanh thu</span><strong>{money(dashboard?.totalRevenue)}</strong></article>
      </section>

      <section className="panel">
        <div className="panel-heading">
          <h2>Hóa đơn chưa thanh toán</h2>
          <button className="ghost-button" onClick={loadDashboard}>Tải lại</button>
        </div>
        <div className="table-wrap">
          <table>
            <thead>
              <tr><th>Tháng</th><th>Phòng</th><th>Người thuê</th><th>Tổng tiền</th><th>Trạng thái</th></tr>
            </thead>
            <tbody>
              {(dashboard?.unpaidInvoices || []).map((invoice) => (
                <tr key={invoice.id}>
                  <td>{invoice.month}</td>
                  <td>{invoice.roomName || invoice.room?.name}</td>
                  <td>{invoice.tenantName || invoice.tenant?.fullName}</td>
                  <td>{money(invoice.totalAmount)}</td>
                  <td><span className="badge unpaid">UNPAID</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
