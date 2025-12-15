CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    username TEXT NOT NULL,
    display_name TEXT,
    profile_image_url TEXT,
    status TEXT DEFAULT 'Offline',
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS chats (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    last_message TEXT,
    last_message_time TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS user_chats (
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    chat_id uuid NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
    other_user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    other_user_name TEXT,
    PRIMARY KEY (user_id, chat_id)
);

CREATE TABLE IF NOT EXISTS messages (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    chat_id uuid NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
    sender_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    text TEXT,
    image_url TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- simple index for ordering messages
CREATE INDEX IF NOT EXISTS idx_messages_chat_time ON messages(chat_id, created_at);

