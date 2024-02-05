import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite';
import vue from '@vitejs/plugin-vue';
import { readFileSync } from 'node:fs';
import path from 'node:path';
import { URL, fileURLToPath } from 'node:url';
import { defineConfig, loadEnv } from 'vite';
import vuetify from 'vite-plugin-vuetify';
import { parseString } from 'xml2js';

// https://vitejs.dev/config/
export default ({ mode }: { mode: string }) => {
  process.env = { ...process.env, ...loadEnv(mode, process.cwd()) };

  const slugify = (value?: string): string => {
    return JSON.stringify(
      String(value)
        .normalize('NFKD') // split accented characters into their base characters and diacritical marks
        .replace(/[\u0300-\u036f]/g, '') // remove all the accents, which happen to be all in the \u03xx UNICODE block.
        .trim() // trim leading or trailing whitespace
        .toLowerCase() // convert to lowercase
        .replace(/[^a-z0-9 -]/g, '') // remove non-alphanumeric characters
        .replace(/\s+/g, '-') // replace spaces with hyphens
        .replace(/-+/g, '-'), // remove consecutive hyphens
    );
  };

  const appName = JSON.stringify(process.env.VITE_APP_NAME);

  const appSlug = (): string => {
    if (process.env.VITE_APP_SLUG) return JSON.stringify(process.env.VITE_APP_SLUG);
    else return slugify(process.env.VITE_APP_NAME);
  };

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
      VueI18nPlugin({
        include: [path.resolve(__dirname, './src/main/webapp/src/locales/**/*.json')],
      }),
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
      hmr: {
        path: 'ws',
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
      __APP_SLUG__: appSlug(),
      __BACK_VERSION__: backVersion(),
    },
  });
};
