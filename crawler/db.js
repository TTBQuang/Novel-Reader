const mysql = require("mysql2/promise");
const bcrypt = require("bcrypt");

const pool = mysql.createPool({
  host: "localhost",
  user: "root",
  password: "123456",
  database: "novel_reader",
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
});

async function getConnection() {
  return pool.getConnection();
}

async function insertGenres(genres) {
  const connection = await getConnection();
  try {
    const query = "INSERT INTO genres (name) VALUES ?";
    const values = genres.map((genre) => [genre]);

    await connection.query(query, [values]);
  } catch (error) {
    console.error("Error inserting genres:", error.message);
  } finally {
    connection.release();
  }
}

async function insertNovel({
  name,
  author,
  artist,
  cover,
  status,
  summary,
  lastUpdate,
  posterId,
  genres,
}) {
  const connection = await getConnection();
  try {
    const [result] = await connection.execute(
      `INSERT INTO novels (name, author, artist, cover, status, summary, updated_at, poster) 
             VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
      [name, author, artist, cover, status, summary, lastUpdate, posterId]
    );

    const insertedId = result.insertId;
    const genreIds = await getGenreIds(genres, connection);

    for (const genreId of genreIds) {
      await connection.execute(
        `INSERT INTO novel_genres (novel_id, genre_id) VALUES (?, ?)`,
        [insertedId, genreId]
      );
    }

    return insertedId;
  } catch (error) {
    console.error("Error inserting novel:", error.message);
    throw error;
  } finally {
    connection.release();
  }
}

async function getGenreIds(genres, connection) {
  const genreIds = [];
  for (const genre of genres.split(";")) {
    const [rows] = await connection.execute(
      `SELECT id FROM genres WHERE name = ?`,
      [genre.trim()]
    );

    if (rows.length > 0) {
      genreIds.push(rows[0].id);
    }
  }
  return genreIds;
}

async function insertChapterGroup(novelId, title) {
  if (!title) {
    return;
  }

  const connection = await getConnection();
  try {
    await connection.beginTransaction();

    const query = `
      INSERT INTO chapter_groups (novel_id, name) VALUES (?, ?)
    `;

    await connection.execute(query, [novelId, title]);

    const [result] = await connection.execute("SELECT LAST_INSERT_ID() AS id");
    const groupId = result[0].id;

    await connection.commit();

    return groupId;
  } catch (error) {
    await connection.rollback();
    console.error("Lỗi khi insert chapter_groups:", error);
  } finally {
    connection.release();
  }
}

async function insertChapter(
  chapterGroupId,
  name,
  content,
  createdAt,
  words_count
) {
  if (!chapterGroupId || !name || !content || !createdAt || !words_count) {
    return;
  }

  const connection = await getConnection();
  try {
    await connection.beginTransaction();

    const query = `
      INSERT INTO chapters (chapter_group_id, name, content, created_at, words_count) 
      VALUES (?, ?, ?, ?, ?)
    `;

    await connection.execute(query, [
      chapterGroupId,
      name,
      content,
      createdAt,
      words_count,
    ]);

    const [result] = await connection.execute("SELECT LAST_INSERT_ID() AS id");
    const chapterId = result[0].id;

    await connection.commit();

    return chapterId;
  } catch (error) {
    await connection.rollback();
    console.error("Lỗi khi insert chapters:", error);
  } finally {
    connection.release();
  }
}

async function insertUser(username, rawPassword) {
  const connection = await getConnection();
  try {
    const saltRounds = 10;
    const hashedPassword = await bcrypt.hash(rawPassword, saltRounds);

    const query =
      "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

    const email = `${username}@gmail.com`;
    await connection.execute(query, [username, hashedPassword, email]);
  } catch (error) {
    console.error("Lỗi khi insert user:", error);
  } finally {
    connection.release();
  }
}

async function getTotalNovels() {
  const connection = await getConnection();
  try {
    const [rows] = await connection.execute(
      "SELECT COUNT(*) AS total FROM novels"
    );
    return rows[0].total;
  } catch (error) {
    console.error("Lỗi khi lấy tổng số novels:", error);
    throw error;
  } finally {
    connection.release();
  }
}

async function getTotalChapters() {
  const connection = await getConnection();
  try {
    const [rows] = await connection.execute(
      "SELECT COUNT(*) AS total FROM chapters"
    );
    return rows[0].total;
  } catch (error) {
    console.error("Lỗi khi lấy tổng số chapters:", error);
    throw error;
  } finally {
    connection.release();
  }
}

async function insertReview(user_id, novel_id, content, rate) {
  const connection = await getConnection();
  try {
    if (rate < 1 || rate > 5) {
      throw new Error("Rate must be between 1 and 5");
    }

    const [result] = await connection.execute(
      `INSERT INTO reviews (user_id, novel_id, content, rate) 
             VALUES (?, ?, ?, ?)`,
      [user_id, novel_id, content, rate]
    );

    const insertedId = result.insertId;

    return insertedId;
  } catch (error) {
    console.error("Error inserting review:", error.message);
    throw error;
  } finally {
    connection.release();
  }
}

async function insertFollowedNovel(user_id, novel_id) {
  const connection = await getConnection();
  try {
    const [result] = await connection.execute(
      `INSERT INTO followed_novels (user_id, novel_id) 
             VALUES (?, ?)`,
      [user_id, novel_id]
    );

    return result.insertId;
  } catch (error) {
    console.error("Error inserting followed novel:", error.message);
    throw error;
  } finally {
    connection.release();
  }
}

async function insertCommentInNovel(user_id, novel_id, content) {
  const connection = await getConnection();
  try {
    const [result] = await connection.execute(
      `INSERT INTO comments (user_id, novel_id, content) 
             VALUES (?, ?, ?)`,
      [user_id, novel_id, content]
    );

    return result.insertId;
  } catch (error) {
    console.error("Error inserting comment:", error.message);
    throw error;
  } finally {
    connection.release();
  }
}

async function insertCommentInChapter(user_id, chapter_id, content) {
  const connection = await getConnection();
  try {
    const [result] = await connection.execute(
      `INSERT INTO comments (user_id, chapter_id, content) 
             VALUES (?, ?, ?)`,
      [user_id, chapter_id, content]
    );
    return result.insertId;
  } catch (error) {
    console.error("Error inserting comment:", error.message);
    throw error;
  } finally {
    connection.release();
  }
}

module.exports = {
  insertNovel,
  insertChapterGroup,
  insertGenres,
  insertChapter,
  insertUser,
  getTotalNovels,
  getTotalChapters,
  insertReview,
  insertFollowedNovel,
  insertCommentInNovel,
  insertCommentInChapter,
};
