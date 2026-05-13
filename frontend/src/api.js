const ROOM_API_URL = "/api/room";
const BILLING_API_URL = "/api/billing";

function baseUrlFor(path) {
  if (
    path.startsWith("/auth") ||
    path.startsWith("/dashboard") ||
    path.startsWith("/invoices") ||
    path.startsWith("/payments")
  ) {
    return BILLING_API_URL;
  }

  return ROOM_API_URL;
}

export async function api(path, options = {}) {
  const response = await fetch(`${baseUrlFor(path)}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  });

  if (!response.ok) {
    throw new Error(await response.text());
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export function money(value) {
  return Number(value || 0).toLocaleString("vi-VN") + " đ";
}

export function toNumberPayload(data, fields) {
  const result = { ...data };
  fields.forEach((field) => {
    result[field] = Number(result[field]);
  });
  return result;
}
