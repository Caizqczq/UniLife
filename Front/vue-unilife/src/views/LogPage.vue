<script setup lang="ts">
import request from '../utils/request' // 确认路径是否正确
import * as yup from 'yup'
import {useRouter} from 'vue-router'
import { useField, useForm, } from 'vee-validate'
import { ref } from 'vue'
import { computed } from 'vue'

const token = ref<string>('')
const router = useRouter()

const testEmail = ref('test@example.com')
const testPassword = ref('123456')

//切换功能实现
const transRate = ref('90')
const switchLogin = ref(true);
const switchLoginMethod = ref<boolean>(true);
const switchLoginMethodEvent = () =>{
    switchLoginMethod.value = !switchLoginMethod.value;
    if(switchLoginMethod.value) {
        login_password.value = "";
        login_password_email.value = login_vericode_email.value;
    } else {
        login_vericode.value = "";
        login_vericode_email.value = login_password_email.value;
    }
}
const form_box = ref<HTMLElement | null>(null);
//登录注册切换
const switchLoginEvent = () =>{
    
    switchLogin.value = !switchLogin.value;
    switchLogin.value ? transRate.value = '0' : transRate.value = '90';
    if(form_box.value)
    {
    form_box.value.style.transform = `translateX(${transRate.value}%)`;
    }

    if(switchLoginMethod.value) {
        login_password.value = login_vericode.value = "";
        login_password_email.value = login_vericode_email.value = register_email.value;
    } else {
        register_password.value = register_verifyPassword.value = register_vericode.value = "";
        login_vericode_email.value = login_password_email.value = register_email.value;
    }
}


//表单验证
//注册表单
const RegisterForm = useForm({
        validationSchema : yup.object({
            register_email: yup.string().email("请输入邮箱").required("请输入正确的邮箱"),
            register_password: yup.string().min(6,"密码至少6位").required("请输入密码"),
            register_verifyPassword: yup.string().oneOf([yup.ref('register_verifyPassword')],"两次密码不一致").required("请确认密码"),
            register_vericode: yup.string().required("请输入验证码")
        }),
})
const {value: register_email} = useField('register_email')
const {value: register_password} = useField  ('register_password')
const {value: register_verifyPassword} = useField('register_verifyPassword')
const {value: register_vericode} = useField('register_vericode')//验证码

//登录表单
//密码登录表单
const LoginPasswordForm = useForm({
    validationSchema : yup.object({
        login_password_email: yup.string().email("请输入邮箱").required("请输入正确的邮箱"),
        login_password: yup.string().min(6,"密码至少6位").required("请输入密码")
    }),
})
const{value: login_password_email} = useField('login_password_email')
const{value: login_password} = useField('login_password')

//验证码登录表单
const LoginEmailForm = useForm({
    validationSchema : yup.object({
        login_vericode_email: yup.string().email("请输入邮箱").required("请输入正确的邮箱"),
        login_vericode: yup.string().required("请输入验证码")
    }),
})
const{value:login_vericode_email} = useField('login_vericode_email')
const{value: login_vericode} = useField('login_vericode')

//错误提示
const showErrors = ref(false)
const ErrorsMessage = ref('')
//调用代码检查表单是否有错误信息提示
const checkErrors = () => {
    if(RegisterForm.errors.value.register_email || RegisterForm.errors.value.register_password || RegisterForm.errors.value.register_verifyPassword || RegisterForm.errors.value.register_vericode) {
        showErrors.value = true
        ErrorsMessage.value = '请检查输入是否正确'
    }else if(LoginPasswordForm.errors.value.login_password_email || LoginPasswordForm.errors.value.login_password) {
        showErrors.value = true
        ErrorsMessage.value = '请检查输入是否正确'
    }else if(LoginEmailForm.errors.value.login_vericode_email || LoginEmailForm.errors.value.login_vericode) {
        showErrors.value = true
        ErrorsMessage.value = '请检查输入是否正确'
    }
}

//注册表单提交
const onRegisterSubmit = () => {
    LoginEmailForm.resetForm();
    LoginPasswordForm.resetForm();
    console.log('函数调用成功')
    RegisterForm.handleSubmit((values) => {
        console.log('表单调用成功',values)
        testcode().then((res) => {
            if(res.code === 200) {
                register().then((res) => {
                    if(res.code === 200) {
                        console.log('注册成功')
                        localStorage.setItem('token', res.data.token)
                        router.push({path:'/personal'})
                    } else {
                        console.log('注册失败')
                    }
                })
            } else {
                console.log('注册失败')
            }
        })
    },
    (errors)=>{
        console.log('注册表单调用失败',errors)
    })();
}

//登录表单提交
const onLoginSubmit = () => { 
    if(!switchLoginMethod.value) {
    LoginEmailForm.resetForm();
    RegisterForm.resetForm();
    LoginPasswordForm.handleSubmit((values) => {
        console.log('表单调用成功',values)
        if(login_password_email.value == testEmail.value && login_password.value == testPassword.value) {
            console.log('测试登录成功')
            console.log(router)
            router.push({name:'Home'})
        } else {
        login().then((res) => {
            if(res.code === 200) {
                console.log('密码登录成功')
                localStorage.setItem('token', res.data.token)
            } else {
                console.log('登录失败')
            }
        })
        }
    },
    (errors) => {
        console.log('密码登录表单调用失败',errors)
    })();
    }else {
        LoginPasswordForm.resetForm();
        RegisterForm.resetForm();
        LoginEmailForm.handleSubmit(() => {
            testcode().then((res) => {
                if(res.code === 200) {
                    console.log('邮箱登录成功')
                    localStorage.setItem('token', res.data.token)
                } else {
                    console.log('登录失败')
                }
            })
        },
        (errors)=>{
            console.log("邮箱登录表单调用失败",errors)
        })();
    }
}


//axios接口
//多合一哈多合一
const email = computed(()=>register_email.value ?? login_password_email.value ?? login_vericode_email.value)
const password = computed(()=>register_password.value ?? login_password.value)
const vericode = computed(()=>register_vericode.value ?? login_vericode.value)
//发送邮箱验证码
async function emailcode(){
    const res = await request.get('/users/code', {
        data:{
            email: email.value
        }
    })

    console.log("success")
}

//验证邮箱验证码
async function testcode() {
    const res = await request.post('/users/login/code', {
        data: {
            email: email.value,
            code: vericode.value
        }
    })
    
    return res.data;
}

async function register(){
    const res = await request.post('/users/register', {
        data:{
            email: email.value,
            password: password.value,
            username:null,
            nickname:null,
            studentId:null,
            department:null,
            major:null,
            grade:null,
        }
    })
    return res.data;
}

async function login(){
    const res = await request.post('/users/login', {
        data:{
            email: email.value,
            password: password.value
        }
    })
    return res.data;
}

</script>

<template>
    <title>登录</title>
    <!-- 错误信息显示，现在无法做到精确显示 -->
    <el-alert
        class = "error-msg"
        v-if="showErrors"
        :title=ErrorsMessage
        type="error"
        :closable="true"
        @close="showErrors = false"
        show-icon = "true"
        center/>
        
    <!-- 登录注册表单 -->
    <div class = "container">
        <div ref = "form_box" class = "form-box">
            <!-- 注册表单 -->
            <div class = "register-box" :class = "{hidden: !switchLogin}">
                <h1>register</h1>
                <div class="email-vericode">
                    <input type="text" placeholder="邮箱" v-model="register_email">
                    <button class="vericode-btn" @click = "emailcode">获取验证码</button>
                </div>
                <input type="password" placeholder="密码" v-model="register_password">
                <input type ="password" placeholder = "确认密码" v-model = "register_verifyPassword">
                <input type="text" placeholder="验证码" v-model="register_vericode">
                <button @click = "onRegisterSubmit(),checkErrors()">注册</button>   
            </div>
            <!-- 登录表单 -->
            <div class = "login-box" :class ="{hidden: switchLogin}">
                <!--密码登录-->
                <div class = password-method :class="{hidden: switchLoginMethod}">
                    <h1>login</h1>
                    <input type="text" placeholder="邮箱" v-model="login_password_email">
                    <input type="password" placeholder="密码" v-model="login_password">
                    <button class = "switch-btn" @click="switchLoginMethodEvent">邮箱登录</button>
                </div>
                <!--邮箱登录-->
                <div class = "email-method" :class ="{hidden: !switchLoginMethod}">
                    <h1>login</h1>
                        <input class ="email-text" type="text" placeholder="邮箱" v-model="login_vericode_email">
                        <button class="email-btn" @click="emailcode">获取验证码</button>
                        <input type="text" placeholder="验证码" v-model="login_vericode">
                        <button class = "switch-btn" @click="switchLoginMethodEvent">密码登录</button>
                </div>
                <button @click="onLoginSubmit(),checkErrors()">登录</button>
            </div>      
        </div>
    <div>
        </div>
        <div class="con-box right">
            <h2>欢迎来到<span>UniLife学生论坛</span></h2>
            <p>这是一个专属于大学生的论坛，
                <br>你可以在这里发表自己的观点，
                <br>也可以在这里找到志同道合的朋友</p>
            <img src = "../../public/images/LogPage1.jpg" alt=""></img>
            <p>已有账号</p>
            <button id= "login" @click="switchLoginEvent">去登录</button>
        </div>
        <div class="con-box left">
            <h2>欢迎回到<span>UniLife学生论坛</span></h2>
            <p>快来看看论坛新来的讯息和同学们的消息吧</p>
            <img src = "../../public/images/LogPage2.jpg" alt=""></img>
            <p>没有账户</p>
            <button id= "register" @click="switchLoginEvent">去注册</button>
        </div>
    </div>
</template>

<style scoped>
    *{
        margin: 0;
        padding: 0;
    }

    body{
        height: 100vh;;
        /*弹性布局，水平垂直居中*/
        justify-content: center;
        align-items: center;
        /*渐变背景*/
        background: linear-gradient(200deg, #f3e7e9, #2a4b7c);
    }

    .container{
        background-color: #fff;
        width:943.8px; /* 原值786.5px，再增加20% */
        height:653.4px; /* 原值544.5px，再增加20% */
        border-radius: 5px;
        /* 阴影 */
        box-shadow:5px 5px 5px rgba(0,0,0,0.1);
        /* 相对定位 */
        position: relative;
    }

    .error-msg{
        width:30%;
        position: absolute;
        top:5%;
        left:35%;
        display: flex;
    }

    .form-box{
        /* 独立 */
        position: absolute;
        top:-7%;
        left:2.5%;
        background-color: #d3b7d8;
        width:470px; /* 原值387.2px，再增加20% */
        height:750px; /* 原值665.5px，再增加20% */
        border-radius: 5px;
        box-shadow: 2px 0 10px rgba(0,0,0,0.1);
        display: flex;
        justify-content: center;
        align-items:center;
        z-index: 2;
        /* 透明度 */
        /* 动画过度，先加速后减速 */
        transition:transform 0.5s ease-in-out;

    }

    .register-box{
        display:flex;
        flex-direction: column;
        align-items: center;
        width:100%;
        height:60%
    }

    .login-box{
        display:flex;
        flex-direction: column;
        align-items: center;
        width:100%;
        height:60%
    }

    .email-btn{
        width:20% !important;
        margin-top:20px !important;
        position:relative !important;
        top:8% !important;
        left:2% !important;
    }

    .email-text{
        width: 50% !important;
    }

    .hidden{
        display: none;
        transition:0.5s;
    }

    h1{
        text-align: center;
        margin-bottom:25px;
        /* 大写 */
        text-transform:uppercase;
        color: #fff;
        letter-spacing:5px;
    }

    input{
        background-color: transparent;
        width:70%;
        height:100px;
        color:#fff;
        border:none;
        /* 下边框样式 */
        border-bottom:1px solid rgba(255,255,255,0.4);
        padding: 12px,0;
        text-indent:10px;
        margin: 10px 0;
        font-size:19px;
        letter-spacing:2px;
    }

    input::placeholder{
        font-size: 19px;
        color:#fff;
    }

    input:focus::placeholder{
        opacity:0;
    }

    input:focus{
        color:#a262ad;
        outline:none;
        border-bottom: 1px solid #a262ad80;
        transition:0.5s;
    }

    .form-box button{
        width: 70%;
        margin-top:35px;
        background-color:#f6f6f6;
        outline:none;
        border-radius: 8px;
        padding:13px;
        color:#a262ad;
        letter-spacing:2px;
        border: none;
        cursor:pointer;
    }



    .vericode-btn{
        width: 30% !important;
        padding:6px;
        border-radius: 5px;
        letter-spacing: 4px;
        outline:none;
        cursor:pointer;
    }

    .email-vericode {
        display: flex;
        align-items: center;
        width: 70%;
        margin: 10px 0;
    }

    .email-vericode input {
        width: 70%;
        margin-right: 10px;
    }

    .email-vericode input::placeholder {
        font-size: 19px;
        color: #fff;
    }

    .form-box button:hover{
        background-color:#a262ad;
        color:#f6f6f6;
        transition:background-color 0.5s ease;
    }

    .con-box{
       width:50%;
       display:flex;
       flex-direction: column;
       justify-content: center;
       align-items: center;
       position:absolute;
       top:50%;
       transform:translateY(-50%);

    }

    .con-box.left{
        left:-2%;
    }

    .con-box.right{
        right:-2%;
    }

    .con-box h2{
        color:#8e9aaf;
        font-size:25px;
        font-weight:bold;
        letter-spacing: 3px;
        text-align:center;
        margin-bottom:4px;
    }

    .con-box p{
        font-size:15px;
        letter-spacing: 2px;
        color:#8e9aaf;
        text-align:center;
    }

    .con-box span{
        color: #d3b7d8;
    }

    .con-box img{
        width:217.8px; /* 原值181.5px，再增加20% */
        height:217.8px; /* 原值181.5px，再增加20% */
        opacity:0.9;
        margin:40px 0;
    }   

    .con-box button{
        margin-top:3%;
        background-color:#fff;
        color:#a262ad;
        padding:6px;
        border-radius: 5px;
        letter-spacing: 1px;
        outline:none;
        cursor:pointer;
    }

    .con-box button:hover{
        background-color:#d3b7d8;
        color:#fff;
        outline:none;
    }

    

   
</style>