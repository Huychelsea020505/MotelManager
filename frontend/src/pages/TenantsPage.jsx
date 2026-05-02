import { useEffect, useState } from "react";
import { api, toNumberPayload } from "../api";

const emptyForm = { citizenId: "", fullName: "", birthDate: "", moveInDate: "", roomId: "" };

export default function TenantsPage() {
  const [tenants, setTenants] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    setError("");
    try {
      const [tenantData, roomData] = await Promise.all([api("/tenants"), api("/rooms")]);
      setTenants(tenantData);
      setRooms(roomData);
    } catch (err) {
      setError("Không thể tải dữ liệu người thuê.");
    }
  }

  function editTenant(tenant) {
    setEditingId(tenant.id);
    setForm({
      citizenId: tenant.citizenId,
      fullName: tenant.fullName,
      birthDate: tenant.birthDate,
      moveInDate: tenant.moveInDate,
      roomId: tenant.room?.id || "",
    });
  }

  async function saveTenant(event) {
    event.preventDefault();
    setMessage("");
    setError("");

    try {
      const payload = toNumberPayload(form, ["roomId"]);
      if (editingId) {
        await api(`/tenants/${editingId}`, { method: "PUT", body: JSON.stringify(payload) });
        setMessage("Đã cập nhật người thuê.");
      } else {
        await api("/tenants", { method: "POST", body: JSON.stringify(payload) });
        setMessage("Đã thêm người thuê mới.");
      }
      setForm(emptyForm);
      setEditingId(null);
      loadData();
    } catch (err) {
      setError("Không thể lưu người thuê. Kiểm tra CCCD hoặc trạng thái phòng.");
    }
  }

  async function deleteTenant(id) {
    if (!confirm("Bạn có chắc muốn xóa người thuê này?")) return;
    try {
      await api(`/tenants/${id}`, { method: "DELETE" });
      setMessage("Đã xóa người thuê.");
      loadData();
    } catch (err) {
      setError("Không thể xóa người thuê còn hóa đơn chưa thanh toán.");
    }
  }

  return (
    <div className="content-grid">
      <section className="panel form-panel">
        <div className="panel-heading"><h2>{editingId ? "Sửa người thuê" : "Thêm người thuê"}</h2></div>
        {error && <div className="alert error">{error}</div>}
        {message && <div className="alert success">{message}</div>}
        <form className="data-form" onSubmit={saveTenant}>
          <label>CCCD<input value={form.citizenId} maxLength="12" onChange={(event) => setForm({ ...form, citizenId: event.target.value })} required /></label>
          <label>Họ tên<input value={form.fullName} onChange={(event) => setForm({ ...form, fullName: event.target.value })} required /></label>
          <label>Ngày sinh<input type="date" value={form.birthDate} onChange={(event) => setForm({ ...form, birthDate: event.target.value })} required /></label>
          <label>Ngày vào ở<input type="date" value={form.moveInDate} onChange={(event) => setForm({ ...form, moveInDate: event.target.value })} required /></label>
          <label>
            Phòng
            <select value={form.roomId} onChange={(event) => setForm({ ...form, roomId: event.target.value })} required>
              <option value="">Chọn phòng</option>
              {rooms.map((room) => <option key={room.id} value={room.id}>{room.name} - {room.status}</option>)}
            </select>
          </label>
          <div className="form-actions">
            <button className="primary-button">{editingId ? "Cập nhật" : "Thêm người thuê"}</button>
            <button type="button" className="ghost-button" onClick={() => { setForm(emptyForm); setEditingId(null); }}>Hủy</button>
          </div>
        </form>
      </section>

      <section className="panel">
        <div className="panel-heading"><h2>Danh sách người thuê</h2><button className="ghost-button" onClick={loadData}>Tải lại</button></div>
        <div className="table-wrap">
          <table>
            <thead><tr><th>CCCD</th><th>Họ tên</th><th>Ngày sinh</th><th>Ngày vào ở</th><th>Phòng</th><th>Hành động</th></tr></thead>
            <tbody>
              {tenants.map((tenant) => (
                <tr key={tenant.id}>
                  <td>{tenant.citizenId}</td>
                  <td>{tenant.fullName}</td>
                  <td>{tenant.birthDate}</td>
                  <td>{tenant.moveInDate}</td>
                  <td>{tenant.room?.name}</td>
                  <td className="actions">
                    <button className="small-button" onClick={() => editTenant(tenant)}>Sửa</button>
                    <button className="small-button danger" onClick={() => deleteTenant(tenant.id)}>Xóa</button>
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
