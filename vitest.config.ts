import viteConfig from './vite.config';
import { fileURLToPath } from 'node:url';
import { configDefaults, defineConfig, mergeConfig } from 'vitest/config';

export default mergeConfig(
  viteConfig({ mode: 'test' }),
  defineConfig({
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude],
      root: fileURLToPath(new URL('./src/test/webapp/spec', import.meta.url)),
      server: {
        deps: {
          inline: ['vuetify'],
        },
      }
    },
  }),
);
