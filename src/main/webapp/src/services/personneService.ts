import { instance as axios } from '@/utils/axiosUtils';

const getPersonne = async (id: number) => await axios.get(`/api/personne/${id}`);

const searchPersonne = async (name: string) => await axios.get(`/api/personne?name=${name}`);

const setPersonneAdditional = async (id: number, structureId: number, additional: Array<string>) =>
  await axios.post(`/api/personne/${id}/fonction`, { structureId, additional });

export { getPersonne, searchPersonne, setPersonneAdditional };
