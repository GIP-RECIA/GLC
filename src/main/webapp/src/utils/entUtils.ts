import vuetify from '@/plugins/vuetify.ts';
import type { PortalEntry } from '@/types/portalEntryType.ts';
import axios from 'axios';

const useEntTheme = async (templateApiPath: string): Promise<void> => {
  const response = await axios.get(templateApiPath);
  const domainInfo: PortalEntry = response.data.data.find((domain: PortalEntry) =>
    domain.identity.domains?.includes(window.location.hostname),
  );

  const primary: string | undefined = domainInfo?.colors.find((color) => color.Id == 'primary')?.hexa;
  if (!primary) return;
  for (const prop in vuetify.theme.themes.value) {
    vuetify.theme.themes.value[prop].colors.primary = primary;
  }
};

export { useEntTheme };
