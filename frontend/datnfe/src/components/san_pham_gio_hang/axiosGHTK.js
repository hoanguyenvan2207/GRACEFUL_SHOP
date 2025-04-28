import axios from 'axios';

const axiosGHTK = axios.create({
  baseURL: '', // không sử dụng baseURL nào, để request được gửi nguyên theo URL đã chỉ định
});

export default axiosGHTK;