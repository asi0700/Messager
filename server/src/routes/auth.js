import { Router } from "express";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import { query } from "../db.js";

const router = Router();
const { JWT_SECRET } = process.env;

const buildToken = (user) =>
  jwt.sign(
    { id: user.id, email: user.email, username: user.username },
    JWT_SECRET,
    { expiresIn: "7d" }
  );

router.post("/register", async (req, res) => {
  const { email, password, username } = req.body || {};
  if (!email || !password || !username) {
    return res.status(400).json({ error: "email, password, username required" });
  }

  try {
    const existing = await query("SELECT id FROM users WHERE email=$1", [email]);
    if (existing.rows.length) {
      return res.status(409).json({ error: "Email already used" });
    }

    const passwordHash = await bcrypt.hash(password, 10);
    const insert = await query(
      `INSERT INTO users (email, password_hash, username, display_name, status)
       VALUES ($1,$2,$3,$3,'Online')
       RETURNING id, email, username, display_name AS "displayName", profile_image_url AS "profileImageUrl", status`,
      [email, passwordHash, username]
    );

    const user = insert.rows[0];
    const token = buildToken(user);
    return res.json({ token, user });
  } catch (err) {
    console.error("[register]", err);
    return res.status(500).json({ error: "Server error" });
  }
});

router.post("/login", async (req, res) => {
  const { email, password } = req.body || {};
  if (!email || !password) {
    return res.status(400).json({ error: "email and password required" });
  }

  try {
    const found = await query(
      `SELECT id, email, username, display_name AS "displayName",
              profile_image_url AS "profileImageUrl", status, password_hash
         FROM users WHERE email=$1`,
      [email]
    );
    if (!found.rows.length) {
      return res.status(401).json({ error: "Invalid credentials" });
    }
    const user = found.rows[0];
    const ok = await bcrypt.compare(password, user.password_hash);
    if (!ok) {
      return res.status(401).json({ error: "Invalid credentials" });
    }
    delete user.password_hash;
    const token = buildToken(user);
    return res.json({ token, user });
  } catch (err) {
    console.error("[login]", err);
    return res.status(500).json({ error: "Server error" });
  }
});

export default router;

