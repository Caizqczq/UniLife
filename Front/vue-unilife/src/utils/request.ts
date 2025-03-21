import axios from 'axios';

const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
});

service.interceptors.request.use(
  config => {
    console.log(1);
    return config;
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  response => {
    console.log(2);
    return response.data;
  },
  error => {
    // 对响应错误做些什么
    return Promise.reject(error);
  }
);

export default service;
