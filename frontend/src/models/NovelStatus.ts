export enum NovelStatus {
  Ongoing = "Đang tiến hành",
  Paused = "Tạm ngưng",
  Completed = "Đã hoàn thành"
}

export const mapNovelStatusToApiValue = (novelStatus: NovelStatus): string => {
  switch (novelStatus) {
    case NovelStatus.Ongoing:
      return "DANG_TIEN_HANH";
    case NovelStatus.Paused:
      return "TAM_NGUNG";
    case NovelStatus.Completed:
      return "DA_HOAN_THANH";
    default:
      return "";
  }
};

export const mapApiStatusToNovelStatus = (apiStatus: string): NovelStatus => {
  switch (apiStatus) {
    case "DANG_TIEN_HANH":
      return NovelStatus.Ongoing;
    case "TAM_NGUNG":
      return NovelStatus.Paused;
    case "DA_HOAN_THANH":
      return NovelStatus.Completed;
    default:
      return apiStatus as NovelStatus;
  }
};