import "dotenv/config";
import express from "express";
import cors from "cors";
import morgan from "morgan";
import path from "path";
import { fileURLToPath } from "url";
import authRoutes from "./routes/auth.js";
import chatRoutes from "./routes/chats.js";
import userRoutes from "./routes/users.js";

const app = express();

const port = process.env.PORT || 8080;
const allowOrigin = process.env.ALLOW_ORIGIN || "*";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const uploadDir = process.env.UPLOAD_DIR || "./uploads";
const uploadsPath = path.isAbsolute(uploadDir)
  ? uploadDir
  : path.join(__dirname, "..", uploadDir);

app.use(cors({ origin: allowOrigin, credentials: true }));
app.use(express.json());
app.use(morgan("dev"));
app.use("/uploads", express.static(uploadsPath));

app.get("/health", (_req, res) => res.json({ ok: true }));

app.use("/auth", authRoutes);
app.use("/", chatRoutes);
app.use("/users", userRoutes);

app.use((err, _req, res, _next) => {
  console.error(err);
  res.status(500).json({ error: "Unexpected error" });
});

app.listen(port, () => {
  console.log(`API running on port ${port}`);
});

