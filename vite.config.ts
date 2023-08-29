import VueI18nPlugin from "@intlify/unplugin-vue-i18n/vite";
import vue from "@vitejs/plugin-vue";
import { dirname, resolve } from "node:path";
import { fileURLToPath, URL } from "node:url";
import { defineConfig, loadEnv } from "vite";
import vuetify from "vite-plugin-vuetify";

// https://vitejs.dev/config/
// @ts-ignore
export default ({ mode }) => {
  process.env = { ...process.env, ...loadEnv(mode, process.cwd()) };

  return defineConfig({
    base: process.env.VITE_BASE_URI + "/ui",
    root: "./src/main/webapp",
    envDir: "../../../",
    plugins: [
      vue({
        template: {
          compilerOptions: {
            isCustomElement: (tag) => tag.includes("extended-uportal-"),
          },
        },
      }),
      vuetify({ autoImport: true }),
      VueI18nPlugin({
        include: resolve(dirname(fileURLToPath(import.meta.url)), "./src/main/webapp/src/locales/**"),
      }),
    ],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src/main/webapp/src", import.meta.url)),
      },
    },
    build: {
      rollupOptions: {
        output: {
          assetFileNames: "assets/glc-[name].[ext]",
          entryFileNames: "assets/glc-[name].js",
          chunkFileNames: "assets/glc-[name].js",
        },
      },
    },
  });
};
