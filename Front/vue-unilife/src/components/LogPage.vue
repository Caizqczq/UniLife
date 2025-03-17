<script setup lang="ts">
import request from '../utils/request' // 确认路径是否正确
import * as yup from 'yup'
import { useField, useForm } from 'vee-validate'
import { ref ,watch} from 'vue'


// 注册登录切换
const transRate = ref('90')
const switchLogin = ref(true);
const switchLoginMethod = ref(true);
const switchLoginMethodEvent = () =>{
    password.value = vericode.value = verifyPassword.value = "";
    switchLoginMethod.value = !switchLoginMethod.value;
    if(switchLoginMethod.value)//邮箱登录切换密码登录
    {
        password.value = verifyPassword.value = 123456;

    }
    else
    {
        vericode.value = 1;
    }
}
const form_box = ref<HTMLElement | null>(null);

//保证界面切换后不出现非对应的错误提示——可优化！！！！！
    const switchLoginEvent = () =>{
    password.value = vericode.value = verifyPassword.value = "";
    if(switchLogin.value)//注册切换登录
    {
        if(switchLoginMethod.value)//邮箱登录切换密码登录
        {
            password.value = verifyPassword.value = 123456;

        }
        else
        {
            vericode.value = 1;
        }
    }    

    switchLogin.value = !switchLogin.value;
    switchLogin.value ? transRate.value = '0' : transRate.value = '90';
    if(form_box.value)
    {
    form_box.value.style.transform = `translateX(${transRate.value}%)`;
    }
}


//表单验证
const {handleSubmit, errors} = useForm({
    validationSchema: yup.object({
        email: yup.string().email("请输入正确的邮箱").required("请输入邮箱"),
        password: yup.string().required("请输入密码").min(6, "密码至少6位"),
        verifyPassword: yup.string().required("请再次输入密码").oneOf([yup.ref('password')], '两次密码不一致'), //验证两次密码是否一致
        vericode: yup.string().required("请输入验证码")
    }),
})

const {value: email} = useField('email')
const {value: password} = useField('password')
const {value: verifyPassword} = useField('verifyPassword')
const {value: vericode} = useField('vericode')//验证码

//错误提示
const showErrors = ref(false)


const onSubmit = () => {
    handleSubmit(() => {
        if(!switchLogin.value)//登录
        {
            if(switchLoginMethod.value)//邮箱登录
            {
                testcode().then((res) => {
                    if(res.code === 200) {
                        console.log('登录成功')
                    } else {
                        console.log('登录失败')
                    }
                })
            }
            else//密码登录_未完成
            {
                
            }
        }
        else//注册
        {
        testcode().then((res) => {
            if(res.code === 200) {
                register().then((res) => {
                    if(res.code === 200) {
                        console.log('注册成功')
                    } else {
                        console.log('注册失败')
                    }
                })
            } else {
                console.log('注册失败')
            }
        })
        }
    })()

    watch(errors, (newErrors) => {
    if(Object.keys(newErrors).length > 0) {
        showErrors.value = true
    } else {
        showErrors.value = false
    }
})
}

//axios接口
//发送邮箱验证码
async function emailcode(){
    const res = await request.get('/auth/email/code', {
        data:{
            email: email.value
        }
    })

    console.log(res.data);
}

//验证邮箱验证码
async function testcode() {
    const res = await request.post('/auth/login/code', {
        data: {
            email: email.value,
            code: vericode.value
        }
    })
    
    return res.data;
}

async function register(){
    const res = await request.post('/auth/register', {
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
</script>

<template>
    <title>登录</title>
    <div class = "container">
        <div ref = "form_box" class = "form-box">
            <!-- 注册表单 -->
            <div class = "register-box" :class = "{hidden: !switchLogin}">
                <div v-if="showErrors" class="error-msg">
                    <p v-show="Object.values(errors)[0]">{{ Object.values(errors)[0] }}</p>
                </div>
                <h1>register</h1>
                <div class="email-vericode">
                    <input type="text" placeholder="邮箱" v-model="email">
                    <button class="vericode-btn" @click = "emailcode">获取验证码</button>
                </div>
                <input type="password" placeholder="密码" v-model="password">
                <input type ="password" placeholder = "确认密码" v-model = "verifyPassword">
                <input type="text" placeholder="验证码" v-model="vericode">
                <button @click = "onSubmit">注册</button>   
            </div>
            <!-- 登录表单 -->
            <div class = "login-box" :class ="{hidden: switchLogin}">
                <div v-if="showErrors" class="error-msg">
                    <p v-show="Object.values(errors)[0]">{{ Object.values(errors)[0] }}</p>
                </div>
                <!--密码登录-->
                <div class = password-method :class="{hidden: switchLoginMethod}">
                    <h1>login</h1>
                    <input type="text" placeholder="邮箱" v-model="email">
                    <input type="password" placeholder="密码" v-model="password">
                    <button class = "switch-btn" @click="switchLoginMethodEvent">邮箱登录</button>
                </div>
                <!--邮箱登录-->
                <div class = "email-method" :class ="{hidden: !switchLoginMethod}">
                    <h1>login</h1>
                        <input class ="email-text" type="text" placeholder="邮箱" v-model="email">
                        <button class="email-btn" @click="emailcode">获取验证码</button>
                        <input type="text" placeholder="验证码" v-model="vericode">
                        <button class = "switch-btn" @click="switchLoginMethodEvent">密码登录</button>
                </div>
                <button @click="onSubmit">登录</button>
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
        display:flex;
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

    .error-msg p{
        align-items: center;
        position:absolute;
        flex-direction: column;
        color:#fff;
        font-size:20px;
        top:12%;
        left:37%;
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

<style>
    body{
        height: 100vh;
        /*弹性布局，水平垂直居中*/
        display:flex;
        justify-content: center;
        align-items: center;
        /*渐变背景*/
        background: linear-gradient(200deg, #f3e7e9, #e3eeff);
    }
</style>