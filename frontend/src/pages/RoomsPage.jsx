import { useEffect, useState } from "react";
import { api, money, toNumberPayload } from "../api";

const emptyForm = { name: "", price: "", area: "", waterPrice: "", status: "AVAILABLE" };

export default function RoomsPage() {
  const [rooms, setRooms] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    loadRooms();
  }, []);

  async function loadRooms() {
    setError("");
    try {
      setRooms(await api("/rooms"));
    } catch (err) {
      setError("Không thể tải danh sách phòng.");
    }
  }

  function editRoom(room) {
    setEditingId(room.id);
    setForm({
      name: room.name,
      price: room.price,
      area: room.area,
      waterPrice: room.waterPrice,
      status: room.status,
    });
  }

  async function saveRoom(event) {
    event.preventDefault();
    setMessage("");
    setError("");

    try {
      const payload = toNumberPayload(form, ["price", "area", "waterPrice"]);
      if (editingId) {
        await api(`/rooms/${editingId}`, { method: "PUT", body: JSON.stringify(payload) });
        setMessage("Đã cập nhật phòng.");
      } else {
        await api("/rooms", { method: "POST", body: JSON.stringify(payload) });
        setMessage("Đã thêm phòng mới.");
      }
      setForm(emptyForm);
      setEditingId(null);
      loadRooms();
    } catch (err) {
      setError("Không thể lưu phòng. Tên phòng có thể đã tồn tại.");
    }
  }

  async function deleteRoom(id) {
    if (!confirm("Bạn có chắc muốn xóa phòng này?")) return;
    try {
      await api(`/rooms/${id}`, { method: "DELETE" });
      setMessage("Đã xóa phòng.");
      loadRooms();
    } catch (err) {
      setError("Không thể xóa phòng đang có người thuê.");
    }
  }

  return (
    <div className="content-grid">
      <section className="panel form-panel">
        <div className="panel-heading"><h2>{editingId ? "Sửa phòng" : "Thêm phòng"}</h2></div>
        {error && <div className="alert error">{error}</div>}
        {message && <div className="alert success">{message}</div>}
        <form className="data-form" onSubmit={saveRoom}>
          <label>Tên phòng<input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} required /></label>
          <label>Giá phòng<input type="number" value={form.price} onChange={(event) => setForm({ ...form, price: event.target.value })} required /></label>
          <label>Diện tích<input type="number" value={form.area} onChange={(event) => setForm({ ...form, area: event.target.value })} required /></label>
          <label>Giá nước<input type="number" value={form.waterPrice} onChange={(event) => setForm({ ...form, waterPrice: event.target.value })} required /></label>
          <label>
            Trạng thái
            <select value={form.status} onChange={(event) => setForm({ ...form, status: event.target.value })}>
              <option>AVAILABLE</option>
              <option>OCCUPIED</option>
              <option>MAINTENANCE</option>
            </select>
          </label>
          <div className="form-actions">
            <button className="primary-button">{editingId ? "Cập nhật" : "Thêm phòng"}</button>
            <button type="button" className="ghost-button" onClick={() => { setForm(emptyForm); setEditingId(null); }}>Hủy</button>
          </div>
        </form>
      </section>

      <section className="panel">
        <div className="panel-heading"><h2>Danh sách phòng</h2><button className="ghost-button" onClick={loadRooms}>Tải lại</button></div>
        <div className="table-wrap">
          <table>
            <thead><tr><th>Tên phòng</th><th>Diện tích</th><th>Giá phòng</th><th>Giá nước</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
            <tbody>
              {rooms.map((room) => (
                <tr key={room.id}>
                  <td>{room.name}</td>
                  <td>{room.area} m2</td>
                  <td>{money(room.price)}</td>
                  <td>{money(room.waterPrice)}</td>
                  <td><span className={`badge ${room.status?.toLowerCase()}`}>{room.status}</span></td>
                  <td className="actions">
                    <button className="small-button" onClick={() => editRoom(room)}>Sửa</button>
                    <button className="small-button danger" onClick={() => deleteRoom(room.id)}>Xóa</button>
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
