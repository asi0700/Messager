import { Router } from "express";
import multer from "multer";
import path from "path";
import fs from "fs";
import { query } from "../db.js";
import { requireAuth } from "../middleware/auth.js";

const router = Router();

const uploadDir = process.env.UPLOAD_DIR || "./uploads";
if (!fs.existsSync(uploadDir)) {
  fs.mkdirSync(uploadDir, { recursive: true });
}

const storage = multer.diskStorage({
  destination: (_req, _file, cb) => cb(null, uploadDir),
  filename: (_req, file, cb) => {
    const ext = path.extname(file.originalname) || ".jpg";
    cb(null, `${Date.now()}-${Math.random().toString(16).slice(2)}${ext}`);
  },
});

const upload = multer({ storage });

router.get("/user_chats/:userId", requireAuth, async (req, res) => {
  const { userId } = req.params;
  try {
    const result = await query(
      `SELECT uc.chat_id AS "chatId",
              uc.other_user_id AS "otherUserId",
              uc.other_user_name AS name,
              c.last_message AS "lastMessage",
              EXTRACT(EPOCH FROM COALESCE(c.last_message_time, c.created_at)) * 1000 AS "lastMessageTime"
       FROM user_chats uc
       JOIN chats c ON c.id = uc.chat_id
       WHERE uc.user_id=$1
       ORDER BY COALESCE(c.last_message_time, c.created_at) DESC`,
      [userId]
    );
    return res.json({ chats: result.rows });
  } catch (err) {
    console.error("[user_chats]", err);
    return res.status(500).json({ error: "Server error" });
  }
});

router.get("/chats/:chatId/messages", requireAuth, async (req, res) => {
  const { chatId } = req.params;
  const limit = Math.min(Number(req.query.limit) || 200, 500);
  try {
    const result = await query(
      `SELECT id, chat_id AS "chatId", sender_id AS "senderId",
              text, image_url AS "imageUrl",
              EXTRACT(EPOCH FROM created_at) * 1000 AS "timestamp"
       FROM messages
       WHERE chat_id=$1
       ORDER BY created_at ASC
       LIMIT $2`,
      [chatId, limit]
    );
    return res.json({ messages: result.rows });
  } catch (err) {
    console.error("[messages]", err);
    return res.status(500).json({ error: "Server error" });
  }
});

router.post("/chats/start", requireAuth, async (req, res) => {
  const { otherUserId } = req.body || {};
  const userId = req.user.id;
  if (!otherUserId) {
    return res.status(400).json({ error: "otherUserId required" });
  }

  try {
    // Reuse chat if exists
    const existing = await query(
      `SELECT chat_id FROM user_chats WHERE user_id=$1 AND other_user_id=$2`,
      [userId, otherUserId]
    );
    if (existing.rows.length) {
      return res.json({ chatId: existing.rows[0].chat_id });
    }

    const chatInsert = await query(
      `INSERT INTO chats (last_message, last_message_time)
       VALUES (NULL, NULL) RETURNING id`,
      []
    );
    const chatId = chatInsert.rows[0].id;

    const otherUser = await query(
      `SELECT username FROM users WHERE id=$1`,
      [otherUserId]
    );
    const currentUser = await query(
      `SELECT username FROM users WHERE id=$1`,
      [userId]
    );

    await query(
      `INSERT INTO user_chats (user_id, chat_id, other_user_id, other_user_name)
       VALUES ($1,$2,$3,$4), ($3,$2,$1,$5)`,
      [userId, chatId, otherUserId, otherUser.rows[0]?.username || "", currentUser.rows[0]?.username || ""]
    );

    return res.json({ chatId });
  } catch (err) {
    console.error("[start chat]", err);
    return res.status(500).json({ error: "Server error" });
  }
});

router.post("/chats/:chatId/messages", requireAuth, async (req, res) => {
  const { chatId } = req.params;
  const { text } = req.body || {};
  const senderId = req.user.id;
  if (!text) {
    return res.status(400).json({ error: "text required" });
  }

  try {
    const insert = await query(
      `INSERT INTO messages (chat_id, sender_id, text)
       VALUES ($1,$2,$3)
       RETURNING id, chat_id AS "chatId", sender_id AS "senderId",
                 text, image_url AS "imageUrl",
                 EXTRACT(EPOCH FROM created_at) * 1000 AS "timestamp"`,
      [chatId, senderId, text]
    );
    await query(
      `UPDATE chats SET last_message=$1, last_message_time=now() WHERE id=$2`,
      [text, chatId]
    );
    return res.status(201).json({ message: insert.rows[0] });
  } catch (err) {
    console.error("[send text]", err);
    return res.status(500).json({ error: "Server error" });
  }
});

router.post(
  "/chats/:chatId/messages/image",
  requireAuth,
  upload.single("file"),
  async (req, res) => {
    const { chatId } = req.params;
    const senderId = req.user.id;
    if (!req.file) {
      return res.status(400).json({ error: "file required" });
    }
    const urlPath = `/uploads/${req.file.filename}`;

    try {
      const insert = await query(
        `INSERT INTO messages (chat_id, sender_id, text, image_url)
         VALUES ($1,$2,$3,$4)
         RETURNING id, chat_id AS "chatId", sender_id AS "senderId",
                   text, image_url AS "imageUrl",
                   EXTRACT(EPOCH FROM created_at) * 1000 AS "timestamp"`,
        [chatId, senderId, req.body.caption || "", urlPath]
      );
      await query(
        `UPDATE chats SET last_message=$1, last_message_time=now() WHERE id=$2`,
        ["Фото", chatId]
      );
      return res.status(201).json({ message: insert.rows[0] });
    } catch (err) {
      console.error("[send image]", err);
      return res.status(500).json({ error: "Server error" });
    }
  }
);

export default router;

