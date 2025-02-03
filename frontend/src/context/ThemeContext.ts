import { createContext } from "react";

export const ThemeContext = createContext<{
  theme: string;
  changeTheme: () => void;
}>({
  theme: "light",
  changeTheme: () => { },
});
