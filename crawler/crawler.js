const axios = require("axios");
const cheerio = require("cheerio");
const fs = require("fs");
const csv = require("csv-parser");
const path = require("path");

const {
  insertNovel,
  insertChapterGroup,
  insertGenres,
  insertChapter,
  insertUser,
  getTotalNovels,
  getTotalChapters,
  insertFollowedNovel,
  insertCommentInNovel,
  insertCommentInChapter,
} = require("./db");

const BASE_URL =
  "https://docln.sbs/the-loai/romance?truyendich=1&dangtienhanh=1&tamngung=1&hoanthanh=1&sapxep=sotu&page=";
const BASE_NOVEL_URL = "https://docln.sbs";
const filePath = path.join(__dirname, "user_data.csv");
const usersData = fs.readFileSync(filePath, "utf8").split("\n");
const filePathComment = path.join(__dirname, "comment_texts.csv");
const comments = fs.readFileSync(filePathComment, "utf8").split("\n");
const COMMENT_IN_NOVEL_COUNT = 200;
const COMMENT_IN_CHAPTER_COUNT = 300;
const FOLLOWER_COUNT = 10000;

const axiosInstance = axios.create({
  headers: {
    "User-Agent":
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36",
  },
});

async function fetchPage(url) {
  try {
    const response = await axiosInstance.get(url);
    return response.data;
  } catch (error) {
    if (error.response && error.response.status === 429) {
      console.warn(`Hit rate limit. Retrying after 60 seconds...`);
      await sleep(60000);
      return fetchPage(url);
    } else {
      console.error(`Error fetching ${url}:`, error.message);
      return null;
    }
  }
}

function parseNovelsInPage(html) {
  const $ = cheerio.load(html);
  const novels = [];

  $(".thumb-item-flow").each((_, element) => {
    const detailLink = $(element).find(".series-title a").attr("href");
    const novelUrl = detailLink ? `${BASE_NOVEL_URL}${detailLink}` : null;

    if (novelUrl) {
      novels.push({ novelUrl });
    }
  });

  return novels;
}

async function fetchNovelDetails(novelUrl) {
  const html = await fetchPage(novelUrl);
  if (!html) return;

  const $ = cheerio.load(html);

  const title = $(".series-name-group .series-name a").text().trim();

  // Lấy thông tin các thể loại
  const genres = $(".series-gernes .series-gerne-item")
    .map((_, el) => $(el).text().trim())
    .get()
    .join(";");

  // Lấy thông tin tác giả, họa sĩ, và tình trạng
  const author = $('.info-item:contains("Tác giả:") .info-value a')
    .text()
    .trim();
  const artist = $('.info-item:contains("Họa sĩ:") .info-value a')
    .text()
    .trim();
  const status = $('.info-item:contains("Tình trạng:") .info-value a')
    .text()
    .trim();

  // Lấy thông tin tóm tắt
  const summary = $(".summary-content").text().trim() || "N/A";

  // Lấy thông tin cover (image URL)
  const coverUrl = $(".a6-ratio .content.img-in-ratio")
    .css("background-image")
    .replace(/^url\(['"]?/, "")
    .replace(/['"]?\)$/, "");

  const lastUpdateDatetime = $(
    '.statistic-item .statistic-name:contains("Lần cuối")'
  )
    .next(".statistic-value")
    .find("time")
    .attr("datetime");

  const lastUpdate = lastUpdateDatetime
    ? new Date(lastUpdateDatetime).toISOString().replace("T", " ").slice(0, 19)
    : null;

  const randomUserId = getRandomUserId();

  const authorToInsert = author === undefined ? null : author;
  const artistToInsert = artist === undefined ? null : artist;

  let insertedNovelId = await insertNovel({
    name: title,
    author: authorToInsert,
    artist: artistToInsert,
    cover: coverUrl,
    status,
    summary,
    lastUpdate,
    posterId: randomUserId,
    genres,
  });

  const groupMap = new Map();

  const processGroups = async () => {
    for (let group of $(
      ".volume-list.at-series.basic-section.volume-mobile.gradual-mobile"
    )) {
      const groupTitle = $(group)
        .find(".sect-title")
        .text()
        .trim()
        .replace(/\*$/, "")
        .trim();

      const styleAttr = $(group).find(".img-in-ratio").attr("style");
      const imageMatch = styleAttr?.match(/url\(['"]?(.*?)['"]?\)/);
      const imageUrl = imageMatch ? imageMatch[1] : null;

      let insertedGroupId = await insertChapterGroup(
        insertedNovelId,
        groupTitle,
        imageUrl
      );

      const chapterDetails = $(group)
        .find(".list-chapters li")
        .map((_, li) => {
          const link = $(li).find(".chapter-name a").attr("href");
          const date = $(li).find(".chapter-time").text().trim();

          const formattedDate = date
            ? new Date(date.split("/").reverse().join("-") + " 00:00:00")
                .toISOString()
                .replace("T", " ")
                .slice(0, 19)
            : null;

          return {
            link: `${BASE_NOVEL_URL}${link}`,
            date: formattedDate,
          };
        })
        .get();

      const chapterContents = [];
      for (let i = 0; i < chapterDetails.length; i++) {
        const link = chapterDetails[i].link;
        console.log(`Fetching content for: ${link}`);

        const content = await fetchChapterContent(link);

        await insertChapter(
          insertedGroupId,
          content.chapterTitle,
          content.paragraphs,
          chapterDetails[i].date,
          content.wordCount
        );

        chapterContents.push(content);

        if (i + 1 < chapterDetails.length) {
          console.log("Waiting 2 seconds before fetching next chapter...");
          await sleep(2000);
        }
      }

      groupMap.set(groupTitle, { chapterContents });
    }
  };

  await processGroups();
}

async function fetchChapterContent(chapterUrl) {
  try {
    const html = await fetchPage(chapterUrl);
    if (!html) return "N/A";

    const $ = cheerio.load(html);

    const chapterTitle = $(".title-top h4.title-item.text-base.font-bold")
      .text()
      .trim();

    // Lấy số từ từ đoạn HTML
    const wordCountText = $(".title-top h6.title-item.font-bold").text().trim();
    const wordCountMatch = wordCountText.match(/Độ dài:\s*([\d,]+)/);
    const wordCount = wordCountMatch
      ? parseInt(wordCountMatch[1].replace(/,/g, ""), 10)
      : 0;

    const paragraphs = $("#chapter-content p")
      .map((_, el) => $.html(el))
      .get()
      .join("");

    return { chapterTitle, paragraphs, wordCount } || "N/A";
  } catch (error) {
    console.error(
      `Error fetching chapter content from ${chapterUrl}:`,
      error.message
    );
    return "N/A";
  }
}

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function crawlNovels() {
  for (let page = 27; page <= 28; page++) {
    console.log(`Fetching page ${page}...`);
    const html = await fetchPage(`${BASE_URL}${page}`);

    if (html) {
      const novels = parseNovelsInPage(html);
      for (const novel of novels) {
        await fetchNovelDetails(novel.novelUrl);
        await sleep(2000);
      }
    }

    await sleep(2000);
  }
}

async function saveGenresToDb() {
  const html = await fetchPage(`${BASE_URL}1`);
  if (html) {
    const $ = cheerio.load(html);

    const genres = [];
    $(".filter-type_item a").each((_, element) => {
      const genre = $(element).text().trim();
      if (genre) {
        genres.push(genre);
      }
    });

    await insertGenres(genres);
    console.log("Genres saved to database");
  } else {
    console.error("Failed to fetch HTML content");
  }
}

async function insertUsers() {
  try {
    const insertPromises = usersData.map((row) => {
      const [username, password, displayName] = row.split(",");
      if (username && password) {
        return insertUser(username, password, displayName);
      }
    });

    await Promise.all(insertPromises);

    console.log("Users inserted!");
  } catch (error) {
    console.log("Users insert failed!", error);
  }
}

function getRandomComment() {
  const randomIndex = Math.floor(Math.random() * comments.length);
  return comments[randomIndex];
}

function getRandomUserId() {
  let userId;

  userId = Math.floor(Math.random() * 100) + 1;

  return userId;
}

async function insertMultipleFollowers() {
  for (let i = 0; i < FOLLOWER_COUNT; i++) {
    const user_id = getRandomUserId();
    const totalNovels = await getTotalNovels();
    const novel_id = Math.floor(Math.random() * totalNovels) + 1;

    try {
      await insertFollowedNovel(user_id, novel_id);
    } catch (error) {
      console.error("Lỗi khi chèn followers:", error.message);
    }
  }
  console.log("Đã chèn followers!");
}

async function insertMultipleCommentsInNovel() {
  for (let i = 0; i < COMMENT_IN_NOVEL_COUNT; i++) {
    const user_id = getRandomUserId();
    const totalNovels = await getTotalNovels();
    const novel_id = Math.floor(Math.random() * totalNovels) + 1;

    try {
      await insertCommentInNovel(user_id, novel_id, getRandomComment());
    } catch (error) {}
  }
  console.log("Đã chèn comments vào novels!");
}

async function insertMultipleCommentsInChapter() {
  for (let i = 0; i < COMMENT_IN_CHAPTER_COUNT; i++) {
    const user_id = getRandomUserId();
    const totalChapters = await getTotalChapters();
    const chapter_id = Math.floor(Math.random() * totalChapters) + 1;

    try {
      await insertCommentInChapter(user_id, chapter_id, getRandomComment());
    } catch (error) {}
  }
  console.log("Đã chèn comments vào chapter!");
}

(async function main() {
  // await saveGenresToDb();
  // await insertUsers();
  await crawlNovels();
  // await insertMultipleFollowers();
  await insertMultipleCommentsInNovel();
  await insertMultipleCommentsInChapter();
  console.log("Finished crawling.");
  process.exit(0);
})();
