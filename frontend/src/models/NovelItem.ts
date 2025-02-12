import { PageInfo } from "./PageInfo";
export interface NovelItem {
  id: number;
  name: string;
  cover: string;
}

export interface NovelsResponse {
  content: NovelItem[];
  page: PageInfo;
}
export interface NovelItemProps {
  novelItem: NovelItem;
}