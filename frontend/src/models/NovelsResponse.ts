import { Novel } from "./Novel";
import { PageInfo } from "./PageInfo";
export interface NovelsResponse {
  content: Novel[];
  page: PageInfo;
}