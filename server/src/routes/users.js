import { Router } from "express";
import { query } from "../db.js";
import { requireAuth } from "../middleware/auth.js";

const router = Router();

router.get("/", requireAuth, async (req, res) => {
  const search = (req.query.q || "").trim();
  try {
    if (!search) {
      const result = await query(
        `SELECT id, email, username, display_name AS "displayName",
                profile_image_url AS "profileImageUrl", status
         FROM users
         ORDER BY created_at DESC
         LIMIT 50`
      );
      return res.json({ users: result.rows });
    }

    const result = await query(
      `SELECT id, email, username, display_name AS "displayName",
              profile_image_url AS "profileImageUrl", status
       FROM users
       WHERE username ILIKE $1 OR email ILIKE $1
       ORDER BY created_at DESC
       LIMIT 50`,
      [`%${search}%`]
    );
    return res.json({ users: result.rows });
  } catch (err) {
    console.error("[list users]", err);
    return res.status(500).json({ error: "Server error" });
  }
});

router.patch("/:id/status", requireAuth, async (req, res) => {
  const { id } = req.params;
  const { status } = req.body || {};
  if (!status) {
    return res.status(400).json({ error: "status required" });
  }
  try {
    const result = await query(
      `UPDATE users SET status=$1 WHERE id=$2
       RETURNING id, email, username, display_name AS "displayName",
                 profile_image_url AS "profileImageUrl", status`,
      [status, id]
    );
    if (!result.rows.length) {
      return res.status(404).json({ error: "User not found" });
    }
    return res.json({ user: result.rows[0] });
  } catch (err) {
    console.error("[status]", err);
    return res.status(500).json({ error: "Server error" });
  }
});

export default router;

