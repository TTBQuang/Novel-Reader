export enum NovelSortOption {
  AZ = "A - Z",
  ZA = "Z - A",
  NewUpdated = "Mới cập nhật",
  NewStories = "Truyện mới",
  WordCount = "Số từ"
}

export const mapNovelSortOptionToApiValue = (sortOption: NovelSortOption): string => {
  switch (sortOption) {
    case NovelSortOption.AZ:
      return "AZ";
    case NovelSortOption.ZA:
      return "ZA";
    case NovelSortOption.NewUpdated:
      return "LAST_UPDATE_DATE";
    case NovelSortOption.NewStories:
      return "CREATION_DATE";
    case NovelSortOption.WordCount:
      return "WORDS_COUNT";
    default:
      return "";
  }
};

export const reverseMapNovelSortOption = (apiValue: string): NovelSortOption => {
  switch (apiValue) {
    case "AZ":
      return NovelSortOption.AZ;
    case "ZA":
      return NovelSortOption.ZA;
    case "LAST_UPDATE_DATE":
      return NovelSortOption.NewUpdated;
    case "CREATION_DATE":
      return NovelSortOption.NewStories;
    case "WORDS_COUNT":
      return NovelSortOption.WordCount;
    default:
      return NovelSortOption.AZ;
  }
};