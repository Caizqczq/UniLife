<script set lang="ts">
import { defineComponent, ref, nextTick ,watch,onMounted} from 'vue';
import{useForm,useField,Form} from 'vee-validate';
import * as yup from 'yup';
import request from '../../utils/request';
import { useGetDerivedNamespace ,ElMessage} from 'element-plus';
import { useEmailCode } from '../useEmailCode';

export default defineComponent({
  name: 'Manager',
  //这个是个人信息的展示和修改界面
  //需要展示的信息有 username，gender，introduction，birthday，email,password(不直接展示，只能看出密码位数，只作为标记作用)
  //其中除了email以外，其他的都可以修改
  //修改username，gender，introduction，birthday，不需要邮箱验证
  //修改密码则需要新密码，确认密码和验证码三个变量
  //现在使用struct orignData{username,gender,introduction,birthday,email,password}来存储原始数据

  //错误信息使用ToolTip显示
  setup() {
    const originData = ref({
      username:"测试员",
      avatarUrl:'/images/默认头像.jpg',
      gender:2,
      introduction:'只要不出bug一切都好QAQ',
      birthday:'2023-10-01',
      email:"test@example.com",
      password:'123456',
    })

    const formData = ref({...originData.value});//修改后的数据
    //个人信息变量
    const isEditable = ref(false);

    //修改密码变量
    const showErrors = ref(false);
    const PasswordEdit = ref(false);
    const newpassword = ref('');
    const newpasswordConfirm = ref('');
    const emailCode = ref(''); //验证码

    const toggleEdit = () => {
      isEditable.value = !isEditable.value;
    };

    const togglePasswordEdit = () => {
      PasswordEdit.value = !PasswordEdit.value;
    };

    //头像相关变量
    const hover = ref(false)
    const dialogVisible = ref(false);
    const previewUrl = ref<string>('');
    const selectedFile = ref<File | null>(null)

    const handleAvatarClick = ()=>
    {
      dialogVisible.value = true;
    }

    const beforeAvatarUpload = (file:File)=>
    {
      const isImage = file.type.startsWith('image/');
      if(!isImage){
        ElMessage.error('只能上传图片文件')
      }
      const isLt2M = file.size / 1024 / 1024 <2
      if(!isLt2M)
      {
        ElMessage.error('图片大小不能超过2MB')
      }

      if(isImage && isLt2M)
      {
        selectedFile.value = file;
        previewUrl.value = URL.createObjectURL(file)
      }
      return false
    }

    const uploadAvatar = async() =>
    {
      if(!selectedFile.value) return 

      const formData = new FormData();
      formData.append('file',selectedFile.value);

      try{
        const res = await request.post('/users/avatar',
          {
            data:{
              file:formData,
            }
          })
          
          if(res.data.code === 200)
          {
            ElMessage.success('头像上传成功')
            previewUrl.value = URL.createObjectURL(selectedFile.value)
          }
          else
          {
            ElMessage.error('头像上传失败')
          }
          
      }catch(error){
        console.error(error)
        ElMessage.error('头像上传失败')
      }
    }

    const submitAvatar = () =>
    {
      uploadAvatar();
    }


    //与alert连接的错误信息，相关的函数已经被我删掉了，因为出现的错误信息滞后显示的bug
    const errorsMeg = ref('');
    

    //表单验证规则
    //个人信息修改表单
    const ProfileScheme = useForm({
        initialValues:formData.value,
        validationSchema:yup.object(
          {
            username:yup.string().required('昵称不能为空'),
            introduction:yup.string().required("自我介绍不能为空"),
          }
        )
    })

    //密码修改表单
    const PasswordSheme = useForm({
      initialValues:{
        newpassword:newpassword.value,
        newpasswordConfirm:newpasswordConfirm.value,
        emailCode:emailCode.value
      },
      validationSchema:yup.object(
        {
          newpassword:yup.string().required('新密码不能为空').min(6,'密码至少6位'),
          newpasswordConfirm:yup.string().required('确认密码不能为空').oneOf([yup.ref('newpassword')],'两次输入的密码不一致'),
          emailCode:yup.string().required('验证码不能为空')
        }
      )
    })

    //表单提交函数
    //个人信息提交表单
    const onProfileSubmit = async() =>{
      console.log("调用个人信息提交函数");
      ProfileScheme.resetForm({values:{...formData.value},
      errors:{}});
      PasswordSheme.resetForm({errors:{}});
      //提交表单   
      ProfileScheme.handleSubmit((values)=>{
        console.log("表单调用成功",values);
        updataUserInfo().then((res)=>
      {
        if(res.code == 200){
          console.log("个人信息保存成功");
          isEditable.value = !isEditable;
        }
        else
        {
          console.log("个人信息保存失败")
        }
      })
      },(errors) => {
      console.log("表单调用失败", errors);
      formData.value = { ...originData.value }; // 恢复原始数据 
    }
    )();
    }

    //密码修改提交表单
    const onPasswordSubmit = async()=>
    {
      console.log("调用密码提交函数");
      PasswordSheme.resetForm({values:{
        newpassword:newpassword.value,
        newpasswordConfirm:newpasswordConfirm.value,
        emailCode:emailCode.value
      },errors:{}});
      ProfileScheme.resetForm({errors:{}});

      PasswordSheme.handleSubmit((values)=>{
        console.log("表单调用成功",values);
        
        modifyPassword().then((res)=>{
        if(res != 200){
          ElMessage.error("验证码错误");
        }
        else{
          console.log("修改成功");
          PasswordEdit.value = false;
        }
      })
        
      },(err) =>{
      console.log("表单调用失败",err);
    })();
    }

    //错误信息显示


    //axios接口
    //从后台获取用户信息
    async function getUserInfo() {
      try {
        const response = await request.get('/user/info');
        console.log('获取用户信息成功:', response.data);

        //从后端获取用户信息
        originData.value.avatarUrl = response.data.avatar;
        originData.value.gender = response.data.gender;
        originData.value.email = response.data.email;
        originData.value.introduction = response.data.bio;
        originData.value.password = "123456";
        originData.value.username = response.data.username;
        originData.value.birthday = response.data.birthday;

        formData.value = { ...originData.value };
      } catch (error) {
        console.error('获取用户信息失败:', error);
      }
    }

    //更新用户信息
    async function updataUserInfo(){
      try{
        const res = request.put('/users/profile',{
          data:{
            username:formData.value.username,
            bio:formData.value.introduction,
            gender:formData.value.gender,
            birthday:formData.value.birthday,
          }
        })
        console.log("向后端发送数据成功：",formData.value);
        return (await res).data;
      }
      catch(error)
      {
        console.log("发送数据失败",error);
      }
    }

    //修改用户密码
    const {sendEmailCode} = useEmailCode()
    async function modifyPassword()
    {
      try{
        const res = await request.put('/users/password',{
          data:{
            code:emailCode,
            newPassword:newpassword
          }
        })
        if(res.data.code == 200) console.log("新密码修改成功")
        else console.log("验证码错误");
        return res.data
      } catch (error) {
        console.error('Password modification failed:', error);
      }
    }

    onMounted(()=>{
      getUserInfo();
    })

    return {
      originData,
      formData,
      isEditable,

      newpassword,
      newpasswordConfirm,
      emailCode,
      sendEmailCode,
      
      PasswordEdit,
      toggleEdit,
      togglePasswordEdit,

      //表单相关变量
      ProfileScheme,
      PasswordSheme,
      onProfileSubmit,
      onPasswordSubmit,

      //错误信息相关变量
      showErrors,
      errorsMeg,

      //头像相关
      hover,
      dialogVisible,
      previewUrl,
      selectedFile,
      handleAvatarClick,
      beforeAvatarUpload,
      uploadAvatar,
      submitAvatar
    };
  }
});


</script>

<template>
  <!--错误信息显示的地方-->
  <transition name = "fade-up">
  <el-alert
        class = "error-msg"
        v-if="showErrors"
        :title= "errorsMeg"
        type="error"
        effect="dark"
        :closable="true"
        @close="showErrors = false"
        show-icon = "true"
        center/>
    </transition>

    <div class = "container">
    <div class="main-content">
      <!-- 左侧信息设置区 -->
      <div class="card profile-info-card">
        <!-- 个人信息部分 -->
        <Form :validation-schema="ProfileScheme">
        <div class="form-section">          
          <h2 class="section-title">个人信息</h2>
          
          <div class="form-group">
            <label class="form-label">昵称</label>          
            <input type="text" class="form-input" v-model="formData.username" :readonly="!isEditable"/>
          </div>
          
          <div class="form-group">
            <label class="form-label">个人简介</label>
            <input type="text" class="form-input" v-model="formData.introduction" :readonly="!isEditable">
          </div>
          
          <div class="form-group">
            <label class="form-label">性别</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" name="gender" class="radio-input" v-model = "formData.gender" :value="2" :disabled="!isEditable">
                <span>女</span>
              </label>
              <label class="radio-label">
                <input type="radio" name="gender" class="radio-input" v-model = "formData.gender" :value="1" :disabled="!isEditable">
                <span>男</span>
              </label>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">生日</label>
            <input type="date" class="date-input" v-model = "formData.birthday" :readonly="!isEditable">
          </div>
        </div>
        
        <div class="btn-save-container">
          <button type = "submit" class="btn btn-primary" @click.prevent="onProfileSubmit()" :disabled = "!isEditable">保 存</button>
          <button type = "button"class="btn btn-secondary" @click="toggleEdit">{{ isEditable ? '取消修改' : '修 改' }}</button>
        </div>
      </Form>
        <div class="divider"></div>

        
        <!-- 账号信息部分 -->
         <Form :validation-schema="PasswordSheme">
        <div class="form-section">
          <h2 class="section-title">账号信息</h2>
          
          <!--展示邮箱，同时提供发送验证码的按钮-->
          <div class="form-group">
          <label class="form-label">绑定邮箱</label>
          <input type="email" class="form-input" v-model="formData.email" readonly>
          <button class="btn btn-primary" v-if="PasswordEdit" @click = "sendEmailCode(formData.email)">发送验证码</button>
          </div>
          
          <div class="form-group">
             <label class="form-label">修改密码</label>
            <div class="password-inputs" v-if = "!PasswordEdit">
              <input type="password" class="form-input" v-model = "formData.password" readonly>
            </div>
            <div class = "password-inputs" v-if = "PasswordEdit">
              <input type="password" class="form-input" v-model = "newpassword" placeholder="新密码"/>
              <input type="password" class="form-input" v-model = "newpasswordConfirm" placeholder="确认新密码"/>
              <input type="email-code" class="form-input" v-model="emailCode" placeholder="验证码">
            </div>
            <button type ="button" class="btn btn-primary btn-password" @click = "togglePasswordEdit()">
              {{ PasswordEdit ? '取消' : '修改' }}
            </button>
            <button type = "submit" class="btn btn-secondary btn-password" v-if="PasswordEdit" @click.prevent="onPasswordSubmit()">
              保存
            </button>
          </div>
          <p class="form-hint">* 密码至少包含6个字符，建议使用字母、数字和符号的组合</p>
        </div>

        </Form>
        
      </div>
      
      <!-- 右侧预览区 -->
      <div class="card profile-preview-card">
        <div class="preview-section">
          <div class="avatar-wrapper" @click = "handleAvatarClick" @mouseenter = "hover = true" @mouseleave = "hover = false">
            <img :src="originData.avatarUrl" alt="用户头像" class = "avatar-image">
            <div v-if = "hover" class = "avatar-hover-mask">更换头像</div>
          </div>

          <el-dialog 
          v-model = "dialogVisible" 
          title = "更换头像" 
          width="70%" 
          :show-close="false">
            <el-upload
                class="avatar-uploader"
                :http-request = "uploadAvatar"
                :show-file-list="false"
                :before-upload = "beforeAvatarUpload"
              >
                <img v-if="previewUrl" :src="previewUrl" class="avatar-preview"/>
                <el-icon size = 30px v-else><Plus/></el-icon>
            </el-upload>
            <template #footer>
              <el-button class = "btn btn-secondary"@click = "dialogVisible = false;previewUrl = ''">取消</el-button>
              <el-button class = "btn btn-primary" type="primary" @click="submitAvatar">确认</el-button>
            </template>
          </el-dialog>
          
          <h3>{{ formData.username }}</h3>
          
          <p class="preview-email">{{ formData.email }}</p>
          
          <div class="preview-bio">
            {{ formData.introduction }}
          </div>
        </div>
        
        <!-- 可爱装饰元素 -->
        <div class="cute-decoration star-1">✨</div>
        <div class="cute-decoration star-2">✨</div>
        <div class="cute-decoration heart">💜</div>
        <div class="cute-decoration cat">🐱</div>
        <div class="cute-decoration cake">🥰🍰</div>
      </div>
    </div>
    </div>
</template>

<style scoped>

#app {
  width: 100%;
  max-width: none;
  padding: 0;
  margin: 0;
}

.error-msg{
        z-index: 1000;
        height:50px;
        width:30%;
        position: absolute;
        top:8%;
        left:20%;
        display: flex;
        transition:2s;
    }

/*错误信息提示*/
.fade-up-enter-active,
.fade-up-leave-active{
  transition: all 0.4s ease;
}

.fade-up-enter-from,
.fade-up-leave-to{
  opacity:0;
  transform:translateY(10px);
}

.fade-up-enter-to,
.fade-up-leave-from{
  opacity:1;
  transform:translateY(0);
}

.hidden
{
  display: none;
}
  /* 主容器 */
.container {
    position:absolute;  
    background-color:transparent;
    left:100px;
    top:2%;
    width:94%;
    height: 96%;
}

/* 侧边栏 */

/* 用户头像容器 */
.avatar-container {
  display: flex;
  justify-content: center;
  margin: 20px 0 30px;
}

.avatar-wrapper{
  position:relative;
  width: 200px;
  height:200px;
  cursor:pointer;
  border-radius:50%;
  overflow:hidden;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.avatar-image{
  width:100%;
  height:100%;
  object-fit:cover;
}

.avatar-hover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.avatar-uploader {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 150px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}


/* 主内容区 */
.main-content {
  flex: 1;
  height:94%;
  padding: 30px;
  display: flex;
  gap: 20px;
}

/* 信息卡片通用样式 */
.card {
  background-color: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

/* 左侧内容卡片 */
.profile-info-card {
  flex: 2;
}

/* 右侧内容卡片 */
.profile-preview-card {
  padding-top:90px;
  flex: 1;
}

/* 分隔线 */
.divider {
  height: 1px;
  background-color: #e6e6fa;
  margin: 25px 0;
  border-radius: 1px;
}

/* 表单区 */
.form-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 2rem;
  color: #9370DB;
  margin-bottom: 20px;
  font-weight: 500;
}

.form-group {
  height:60px;
  margin-bottom: 15px;
  margin-left:10px;
  display: flex;
  align-items: center;
}

.form-label {
  width: 95px;
  font-size: 1.25rem;
  color: #666;
  text-align:left;
}

/* 输入框样式 */
.form-input {
  flex: 1;
  height:30px;
  padding: 10px 15px;
  border: 2px solid #e6e6fa;
  border-radius: 25px;
  outline: none;
  transition: border-color 0.3s ease;
  font-size: 1.2rem;
}

.form-input:focus {
  border-color: #b19cd9;
}

/* 单选按钮样式 */
.radio-group {
  transform:scale(1.2);
  display: flex;
  gap: 30px;
}

.radio-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.radio-input {
  appearance: none;
  -webkit-appearance: none;
  width: 20px;
  height: 20px;
  border: 2px solid #e6e6fa;
  border-radius: 50%;
  margin-right: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.radio-input:checked {
  border-color: #9370DB;
}

.radio-input:checked::before {
  content: "";
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #9370DB;
}

/* 日期选择器 */
.date-input {
  width: 100%;
  padding: 10px 15px;
  border: 2px solid #e6e6fa;
  border-radius: 25px;
  outline: none;
  transition: border-color 0.3s ease;
  background-color: #fff;
  font-size:1.2rem;
}

.date-input:focus {
  border-color: #b19cd9;
}

/* 按钮样式 */
.btn {
  outline:none;
  padding: 10px 24px;
  margin:10px;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
  background-color: #fff;
}

.btn-primary {
  background-color: #9370DB;
  color: white;
  box-shadow: 0 4px 10px rgba(147, 112, 219, 0.3);
}

.btn-primary:hover {
  background-color: #8a63d2;
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(147, 112, 219, 0.4);
}

.btn-save-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.btn-secondary {
  background-color: #e6e6fa;
  color: #666;
  box-shadow: 0 4px 10px rgba(230, 230, 250, 0.3);
}

.btn-secondary:hover {
  background-color: #dcdcdc;
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(230, 230, 250, 0.4);
}

/* 预览区样式 */
.preview-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.preview-avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 5px solid #fff;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 10px;
}

.preview-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-email {
  color: #666;
  font-size: 1.2rem;
  margin-bottom: 15px;
  margin-top:0px;
}

.preview-bio {
  width: 100%;
  min-height: 100px;
  padding: 15px;
  border: 2px solid #e6e6fa;
  border-radius: 15px;
  background-color: #f9f7ff;
  font-size: 1.2rem;
  color: #666;
}

/* 密码修改区域样式 */
.password-inputs {
  display: flex;
  gap: 10px;
  margin-right: 10px;
}

.password-inputs .form-input {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    padding: 10px;
  }
  
  .menu-item {
    border-radius: 25px;
    margin-right: 0;
    padding: 10px 15px;
    text-align: center;
  }
  
  .main-content {
    padding: 15px;
  }
  
  .form-group {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .form-label {
    width: 100%;
    margin-bottom: 5px;
  }
  
  .password-inputs {
    flex-direction: column;
  }
}

/* 可爱元素：气泡背景 */
.bubble {
  position: absolute;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.3);
  z-index: -1;
  animation: float 15s ease-in-out infinite;
}

.bubble-1 {
  width: 100px;
  height: 100px;
  bottom: 50px;
  left: 20px;
  animation-delay: 0s;
}

.bubble-2 {
  width: 60px;
  height: 60px;
  top: 100px;
  left: 40px;
  animation-delay: 2s;
}

.bubble-3 {
  width: 80px;
  height: 80px;
  bottom: 200px;
  left: 60px;
  animation-delay: 5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card {
  animation: fadeIn 0.5s ease forwards;
}

.profile-preview-card {
  animation-delay: 0.2s;
}

/* 提示文本样式 */
.form-hint {
  font-size: 0.8rem;
  color: #9370DB;
  margin-top: 5px;
  margin-left: 100px;
}

/* 可爱装饰元素 */
.cute-decoration {
  position: absolute;
  font-size: 1.5rem;
  opacity: 0.3;
  color: #9370DB;
  pointer-events: none;
}

.star-1 {
  top: 50px;
  right: 20px;
}

.star-2 {
  bottom: 100px;
  right: 40px;
}

.heart {
  bottom: 30px;
  right: 30px;
}

</style>