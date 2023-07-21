export type Identity = {
  user: any;
  roles: Array<string>;
  sessionId?: string;
  username: string;
  accountNonExpired: boolean;
  accountNonLocked: boolean;
  credentialsNonExpired: boolean;
  enabled: boolean;
  password?: string;
};
