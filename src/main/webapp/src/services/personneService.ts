import { instance as axios } from '@/utils/axiosUtils.ts';

const getPersonne = async (id: number) => await axios.get(`/api/personne/${id}`);

const searchPersonne = async (name: string) => await axios.get(`/api/personne?name=${name}`);

const setPersonneAdditional = async (
  id: number,
  structureId: number,
  toAddFunctions: Array<string>,
  toDeleteFunctions: Array<string>,
  requiredAction: string,
) =>
  await axios.post(`/api/personne/${id}/fonction`, { structureId, toAddFunctions, toDeleteFunctions, requiredAction });

const setPersonneAdditionalWithId = async (
  id: number,
  structureId: number,
  toAddFunction: string,
  requiredAction: string,
) => await setPersonneAdditional(id, structureId, [toAddFunction], [], requiredAction);

const setPersonneAdditionalWithCode = async (
  id: number,
  structureId: number,
  additionalCode: string,
  requiredAction: string,
) => await axios.post(`/api/personne/${id}/fonction`, { structureId, additionalCode, requiredAction });

export {
  getPersonne,
  searchPersonne,
  setPersonneAdditional,
  setPersonneAdditionalWithId,
  setPersonneAdditionalWithCode,
};
