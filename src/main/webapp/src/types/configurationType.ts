export type Configuration = {
  permissionTypes: Array<string>;
  front: {
    endFunctionWarning: number;
    staff: {
      teaching: string;
      school: string;
      collectivity: string;
      academic: string;
    };
    editAllowedStates: Array<string>;
    filterAccountStates: Array<string>;
    extendedUportalHeader: {
      componentPath: string;
      contextApiUrl: string;
      signOutUrl: string;
      defaultOrgLogoPath: string;
      defaultAvatarPath: string;
      defaultOrgIconPath: string;
      favoriteApiUrl: string;
      layoutApiUrl: string;
      organizationApiUrl: string;
      portletApiUrl: string;
      userInfoApiUrl: string;
      userInfoPortletUrl: string;
      sessionApiUrl: string;
      templateApiPath: string;
      switchOrgPortletUrl: string;
      favoritesPortletCardSize: string;
      gridPortletCardSize: string;
      hideActionMode: string;
      showFavoritesInSlider: string;
      returnHomeTitle: string;
      returnHomeTarget: string;
      iconType: string;
    };
    extendedUportalFooter: {
      componentPath: string;
      templateApiPath: string;
    };
    loginOffices: Array<{
      source: string;
      guichets: Array<{
        nom: string;
        categoriesPersonne: Array<string>;
      }>;
    }>;
  };
};
