import { instance as axios } from '@/utils/axiosUtils.ts';

const getFonctions = async () => await axios.get('/api/fonction');

export { getFonctions };
