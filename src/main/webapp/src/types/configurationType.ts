export type Configuration = {
  administrativeCodes: Array<string>;
  administrativeStaff: Array<string>;
  editAllowedStates: Array<string>;
  filterAccountStates: Array<string>;
  permissionTypes: Array<string>;
  teachingCodes: Array<string>;
  loginOffices: Array<{
    source: string;
    guichets: Array<{
      nom: string;
      categoriesPersonne: Array<string>;
    }>;
  }>;
};
