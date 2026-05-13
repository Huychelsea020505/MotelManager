import { useEffect, useState } from "react";
import { api, money } from "../api";

export default function PaymentsPage() {
  const [payments, setPayments] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    loadPayments();
  }, []);

  async function loadPayments() {
    setError("");
    try {
      setPayments(await api("/payments/history"));
    } catch (err) {
      setError("Không thể tải lịch sử thanh toán.");
    }
  }

  return (
    <section className="panel">
      <div className="panel-heading">
        <h2>Lịch sử thanh toán</h2>
        <button className="ghost-button" onClick={loadPayments}>Tải lại</button>
      </div>

      {error && <div className="alert error">{error}</div>}

      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Mã</th><th>Tháng</th><th>Phòng</th><th>Người thuê</th><th>Số tiền</th><th>Thời gian</th><th>Ghi chú</th></tr>
          </thead>
          <tbody>
            {payments.map((payment) => (
              <tr key={payment.id}>
                <td>#{payment.id}</td>
                <td>{payment.invoice?.month}</td>
                <td>{payment.invoice?.roomName || payment.invoice?.room?.name}</td>
                <td>{payment.invoice?.tenantName || payment.invoice?.tenant?.fullName}</td>
                <td>{money(payment.amount)}</td>
                <td>{payment.paidAt ? new Date(payment.paidAt).toLocaleString("vi-VN") : ""}</td>
                <td>{payment.note || "Không có"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
