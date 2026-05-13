import { useEffect, useMemo, useState } from "react";
import { api, money, toNumberPayload } from "../api";

const emptyForm = {
  roomId: "",
  tenantId: "",
  month: "",
  roomPrice: "",
  waterPrice: "",
  electricityPrice: "",
  servicePrice: "",
};

export default function InvoicesPage() {
  const [invoices, setInvoices] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [tenants, setTenants] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [payForm, setPayForm] = useState({ invoice: null, amount: "", note: "" });
  const [filter, setFilter] = useState("ALL");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    setError("");
    try {
      const [invoiceData, roomData, tenantData] = await Promise.all([
        api("/invoices"),
        api("/rooms"),
        api("/tenants"),
      ]);
      setInvoices(invoiceData);
      setRooms(roomData);
      setTenants(tenantData);
    } catch (err) {
      setError("Không thể tải dữ liệu hóa đơn.");
    }
  }

  async function saveInvoice(event) {
    event.preventDefault();
    setMessage("");
    setError("");

    try {
      await api("/invoices", {
        method: "POST",
        body: JSON.stringify(
          toNumberPayload(form, [
            "roomId",
            "tenantId",
            "roomPrice",
            "waterPrice",
            "electricityPrice",
            "servicePrice",
          ])
        ),
      });
      setForm(emptyForm);
      setMessage("Đã tạo hóa đơn.");
      loadData();
    } catch (err) {
      setError("Không thể tạo hóa đơn. Phòng và người thuê phải khớp nhau.");
    }
  }

  async function payInvoice(event) {
    event.preventDefault();
    if (!payForm.invoice) return;

    try {
      await api(`/invoices/${payForm.invoice.id}/pay`, {
        method: "PUT",
        body: JSON.stringify({ amount: Number(payForm.amount), note: payForm.note }),
      });
      setPayForm({ invoice: null, amount: "", note: "" });
      setMessage("Đã thanh toán hóa đơn.");
      loadData();
    } catch (err) {
      setError("Không thể thanh toán hóa đơn.");
    }
  }

  const visibleInvoices = useMemo(() => {
    if (filter === "ALL") return invoices;
    return invoices.filter((invoice) => invoice.status === filter);
  }, [invoices, filter]);

  return (
    <div className="content-grid">
      <section className="panel form-panel">
        <div className="panel-heading"><h2>Tạo hóa đơn</h2></div>
        {error && <div className="alert error">{error}</div>}
        {message && <div className="alert success">{message}</div>}

        <form className="data-form" onSubmit={saveInvoice}>
          <label>
            Phòng
            <select value={form.roomId} onChange={(event) => setForm({ ...form, roomId: event.target.value })} required>
              <option value="">Chọn phòng</option>
              {rooms.map((room) => <option key={room.id} value={room.id}>{room.name}</option>)}
            </select>
          </label>
          <label>
            Người thuê
            <select value={form.tenantId} onChange={(event) => setForm({ ...form, tenantId: event.target.value })} required>
              <option value="">Chọn người thuê</option>
              {tenants.map((tenant) => <option key={tenant.id} value={tenant.id}>{tenant.fullName} - {tenant.room?.name}</option>)}
            </select>
          </label>
          <label>Tháng<input placeholder="05/2026" value={form.month} onChange={(event) => setForm({ ...form, month: event.target.value })} required /></label>
          <label>Tiền phòng<input type="number" value={form.roomPrice} onChange={(event) => setForm({ ...form, roomPrice: event.target.value })} required /></label>
          <label>Tiền nước<input type="number" value={form.waterPrice} onChange={(event) => setForm({ ...form, waterPrice: event.target.value })} required /></label>
          <label>Tiền điện<input type="number" value={form.electricityPrice} onChange={(event) => setForm({ ...form, electricityPrice: event.target.value })} required /></label>
          <label>Phí dịch vụ<input type="number" value={form.servicePrice} onChange={(event) => setForm({ ...form, servicePrice: event.target.value })} required /></label>
          <button className="primary-button">Tạo hóa đơn</button>
        </form>

        {payForm.invoice && (
          <form className="pay-box" onSubmit={payInvoice}>
            <h3>Thanh toán {payForm.invoice.month}</h3>
            <label>Số tiền<input type="number" value={payForm.amount} onChange={(event) => setPayForm({ ...payForm, amount: event.target.value })} required /></label>
            <label>Ghi chú<input value={payForm.note} onChange={(event) => setPayForm({ ...payForm, note: event.target.value })} /></label>
            <div className="form-actions">
              <button className="primary-button">Xác nhận</button>
              <button type="button" className="ghost-button" onClick={() => setPayForm({ invoice: null, amount: "", note: "" })}>Hủy</button>
            </div>
          </form>
        )}
      </section>

      <section className="panel">
        <div className="panel-heading">
          <h2>Danh sách hóa đơn</h2>
          <select className="compact-select" value={filter} onChange={(event) => setFilter(event.target.value)}>
            <option value="ALL">Tất cả</option>
            <option value="UNPAID">Chưa thanh toán</option>
            <option value="PAID">Đã thanh toán</option>
          </select>
        </div>
        <div className="table-wrap">
          <table>
            <thead><tr><th>Tháng</th><th>Phòng</th><th>Người thuê</th><th>Tổng</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
            <tbody>
              {visibleInvoices.map((invoice) => (
                <tr key={invoice.id}>
                  <td>{invoice.month}</td>
                  <td>{invoice.roomName || invoice.room?.name}</td>
                  <td>{invoice.tenantName || invoice.tenant?.fullName}</td>
                  <td>{money(invoice.totalAmount)}</td>
                  <td><span className={`badge ${invoice.status?.toLowerCase()}`}>{invoice.status}</span></td>
                  <td>
                    {invoice.status === "UNPAID" ? (
                      <button className="small-button" onClick={() => setPayForm({ invoice, amount: invoice.totalAmount, note: "" })}>Thanh toán</button>
                    ) : (
                      <span className="muted">Đã xong</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
