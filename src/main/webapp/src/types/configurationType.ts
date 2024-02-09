export type Configuration = {
  administrativeStaff: Array<string>;
  editAllowedStates: Array<string>;
  filterAccountStates: Array<string>;
  permissionTypes: Array<string>;
  loginOffices: Array<{
    source: string;
    guichets: Array<{
      nom: string;
      categoriesPersonne: Array<string>;
    }>;
  }>;
  endFunctionWarning: number;
};
