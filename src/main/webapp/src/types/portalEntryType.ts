export type PortalEntry = {
  identity: {
    Id: string;
    name: string;
    domains?: Array<string>;
    uai?: Array<string>;
  };
  images: Array<{
    Id: string;
    name: string;
    path?: string;
    url?: string;
    opacity?: number;
  }>;
  colors: Array<{
    Id: string;
    hexa: string;
    rgb: {
      r: number;
      g: number;
      b: number;
    };
  }>;
  sponsors: Array<{
    Id: string;
    name: string;
    url?: string;
    logo: {
      path?: string;
      url?: string;
      parameters?: Array<{
        propertie: string;
        value: string;
      }>;
    };
  }>;
};
