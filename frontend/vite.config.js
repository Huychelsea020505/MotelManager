import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api/room": {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/room/, ""),
      },
      "/api/billing": {
        target: "http://localhost:8082",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/billing/, ""),
      },
      "/rooms": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/tenants": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/auth": {
        target: "http://localhost:8082",
        changeOrigin: true,
      },
      "/dashboard": {
        target: "http://localhost:8082",
        changeOrigin: true,
      },
      "/invoices": {
        target: "http://localhost:8082",
        changeOrigin: true,
      },
      "/payments": {
        target: "http://localhost:8082",
        changeOrigin: true,
      },
    },
  },
});
