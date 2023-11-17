import { instance as axios } from '@/utils/axiosUtils.ts';

const getConfiguration = async () => await axios.get('/api/config');

export { getConfiguration };
