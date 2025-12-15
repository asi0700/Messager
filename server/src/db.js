import pg from "pg";

const { Pool } = pg;

const {
  DATABASE_URL,
  DB_SSL = "true",
} = process.env;

if (!DATABASE_URL) {
  console.warn("[db] DATABASE_URL not set");
}

const pool = new Pool({
  connectionString: DATABASE_URL,
  ssl: DB_SSL === "true" ? { rejectUnauthorized: false } : undefined,
});

export const query = (text, params) => pool.query(text, params);

export const getClient = () => pool.connect();

