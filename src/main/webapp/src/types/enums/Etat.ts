import type { enumValues } from "@/types/enumValuesType";

export enum Etat {
  Inconnu = "Inconnu",
  Invalide = "Invalide",
  Valide = "Valide",
  Unpublished = "Unpublished",
  Bloque = "Bloque",
  Delete = "Delete",
  Deleting = "Deleting",
  Incertain = "Incertain",
  Incertain_Export_Add = "Incertain_Export_Add",
  Incertain_Export_Delete = "Incertain_Export_Delete",
  Incertain_Export_Modify = "Incertain_Export_Modify",
}

const getEtat = (etat: string): enumValues => {
  switch (etat) {
    case Etat.Inconnu.toString():
      return { i18n: "person.status.inconnu", color: "" };
    case Etat.Invalide.toString():
      return { i18n: "person.status.invalid", color: "yellow" };
    case Etat.Valide.toString():
      return { i18n: "person.status.valid", color: "green" };
    case Etat.Unpublished.toString():
      return { i18n: "person.status.unpublished", color: "" };
    case Etat.Bloque.toString():
      return { i18n: "person.status.locked", color: "orange" };
    case Etat.Delete.toString():
      return { i18n: "person.status.deleted", color: "gray" };
    case Etat.Deleting.toString():
      return { i18n: "person.status.deleting", color: "" };
    case Etat.Incertain.toString():
      return { i18n: "person.status.uncertain", color: "yellow" };
    case Etat.Incertain_Export_Add.toString():
      return { i18n: "person.status.uncertainExportAdd", color: "" };
    case Etat.Incertain_Export_Delete.toString():
      return { i18n: "person.status.uncertainExportDelete", color: "" };
    case Etat.Incertain_Export_Modify.toString():
      return { i18n: "person.status.uncertainExportModify", color: "" };
    default:
      throw new Error(`Non-existent etat in switch: ${etat}`);
  }
};

export { getEtat };
