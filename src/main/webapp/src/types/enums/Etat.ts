import type { enumValues } from '@/types/enumValuesType';

export enum Etat {
  Inconnu = 'Inconnu',
  Invalide = 'Invalide',
  Valide = 'Valide',
  Unpublished = 'Unpublished',
  Bloque = 'Bloque',
  Delete = 'Delete',
  Deleting = 'Deleting',
  Incertain = 'Incertain',
  Incertain_Export_Add = 'Incertain_Export_Add',
  Incertain_Export_Delete = 'Incertain_Export_Delete',
  Incertain_Export_Modify = 'Incertain_Export_Modify',
}

const getEtat = (etat: string): enumValues => {
  switch (etat) {
    case Etat.Inconnu.toString():
      return { i18n: 'person.status.inconnu', color: '#9E9E9E', icon: 'fas fa-user-secret' };
    case Etat.Invalide.toString():
      return { i18n: 'person.status.invalid', color: '#A5D6A7' };
    case Etat.Valide.toString():
      return { i18n: 'person.status.valid', color: '#4CAF50' };
    case Etat.Unpublished.toString():
      return { i18n: 'person.status.unpublished', color: '#607D8B' };
    case Etat.Bloque.toString():
      return { i18n: 'person.status.locked', color: '#795548', icon: 'fas fa-user-lock' };
    case Etat.Delete.toString():
      return { i18n: 'person.status.deleted', color: '#EF9A9A' };
    case Etat.Deleting.toString():
      return { i18n: 'person.status.deleting', color: '#FF5722', icon: 'fas fa-user-clock' };
    case Etat.Incertain.toString():
      return { i18n: 'person.status.uncertain', color: '#FFEB3B' };
    case Etat.Incertain_Export_Add.toString():
      return { i18n: 'person.status.uncertainExportAdd', color: '#FFEB3B' };
    case Etat.Incertain_Export_Delete.toString():
      return { i18n: 'person.status.uncertainExportDelete', color: '#FFEB3B' };
    case Etat.Incertain_Export_Modify.toString():
      return { i18n: 'person.status.uncertainExportModify', color: '#FFEB3B' };
    default:
      throw new Error(`Non-existent etat in switch: ${etat}`);
  }
};

export { getEtat };
