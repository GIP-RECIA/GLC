import { instance as axios } from '@/utils/axiosUtils.ts';

const getPersonne = async (id: number) => await axios.get(`/api/personne/${id}`);

const searchPersonne = async (name: string) => await axios.get(`/api/personne?name=${name}`);

const setPersonneAdditional = async (id: number, structureId: number, additional: Array<string>) =>
  await axios.post(`/api/personne/${id}/fonction`, { structureId, additional });

const setPersonneAdditionalWithId = async (id: number, structureId: number, additional: string) =>
  await setPersonneAdditional(id, structureId, [additional]);

const setPersonneAdditionalWithCode = async (id: number, structureId: number, additionalCode: string) =>
  await axios.post(`/api/personne/${id}/fonction`, { structureId, additionalCode });

export {
  getPersonne,
  searchPersonne,
  setPersonneAdditional,
  setPersonneAdditionalWithId,
  setPersonneAdditionalWithCode,
};
