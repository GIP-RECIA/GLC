import { slugify } from './src/main/webapp/src/utils/stringUtils.ts';
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite';
import vue from '@vitejs/plugin-vue';
import { readFileSync } from 'node:fs';
import { URL, fileURLToPath } from 'node:url';
import { defineConfig, loadEnv } from 'vite';
import vuetify from 'vite-plugin-vuetify';
import { parseString } from 'xml2js';

// https://vitejs.dev/config/
export default ({ mode }: { mode: string }) => {
  process.env = { ...process.env, ...loadEnv(mode, process.cwd()) };

  const appName = JSON.stringify(process.env.VITE_APP_NAME);

  const appSlug = JSON.stringify(process.env.VITE_APP_SLUG ? process.env.VITE_APP_SLUG : slugify(appName));

  const backVersion = (): string => {
    let version;
    const pomXml = readFileSync('./pom.xml', 'utf8');
    parseString(pomXml, function (err, result) {
      if (err) console.error(err);
      else version = result.project.version[0];
    });

    return JSON.stringify(version);
  };

  return defineConfig({
    base: process.env.VITE_BASE_URI + '/ui',
    root: './src/main/webapp',
    envDir: '../../../',
    plugins: [
      vue({
        template: {
          compilerOptions: {
            isCustomElement: (tag) => ['extended-uportal-header', 'extended-uportal-footer'].includes(tag),
          },
        },
      }),
      vuetify({ styles: 'sass' }),
      VueI18nPlugin({}),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src/main/webapp/src', import.meta.url)),
      },
    },
    server: {
      proxy: {
        '^(?:/[^/]*)?/api': {
          target: process.env.VITE_PROXY_API_URL,
          changeOrigin: true,
        },
        '^(?:/[^/]*)?/app': {
          target: process.env.VITE_PROXY_API_URL,
          changeOrigin: true,
        },
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler',
        },
      },
    },
    build: {
      rollupOptions: {
        output: {
          assetFileNames: 'assets/[name].[ext]',
          entryFileNames: 'assets/[name].js',
          chunkFileNames: 'assets/[name].js',
        },
      },
    },
    define: {
      __APP_NAME__: appName,
      __APP_SLUG__: appSlug,
      __BACK_VERSION__: backVersion(),
    },
  });
};
