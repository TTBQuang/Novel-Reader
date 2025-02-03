export interface NovelItem {
  id: number;
  name: string;
  image: string;
  latestChapter: string;
}

export interface NovelItemProps {
  novelItem: NovelItem;
}