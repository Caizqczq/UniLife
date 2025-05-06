import axios from 'axios';

const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
});

service.interceptors.request.use( 
  config => {
    const token = localStorage.getItem('token');
    if (token) {
    console.log("前端发送信息");
    return config;
    }
    else 
    { console.log("没有token");
      return config;
    }
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  response => {
    console.log("后端返回信息");
    return response.data;
  },
  error => {
    // 对响应错误做些什么
    return Promise.reject(error);
  }
);

export default service;
