import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite';
import vue from '@vitejs/plugin-vue';
import { URL, fileURLToPath } from 'node:url';
import { defineConfig, loadEnv } from 'vite';
import vuetify from 'vite-plugin-vuetify';

// https://vitejs.dev/config/
export default ({ mode }: { mode: string }) => {
  process.env = { ...process.env, ...loadEnv(mode, process.cwd()) };

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
      vuetify({ autoImport: true, styles: 'expose' }),
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
    build: {
      rollupOptions: {
        output: {
          assetFileNames: 'assets/[name].[ext]',
          entryFileNames: 'assets/[name].js',
          chunkFileNames: 'assets/[name].js',
        },
      },
    },
  });
};
