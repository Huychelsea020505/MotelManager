const API_URL = "http://localhost:8080";

export async function api(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
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
