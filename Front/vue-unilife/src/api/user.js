import request from '../utils/request';

// 用户注册
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  });
}

// 用户密码登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  });
}

// 获取邮箱验证码
export function getEmailCode(data) {
  return request({
    url: '/auth/email/code',
    method: 'post',
    data
  });
}

// 邮箱验证码登录
export function loginWithCode(data) {
  return request({
    url: '/auth/login/code',
    method: 'post',
    data
  });
}