<script set lang="ts">
import { defineComponent,ref } from 'vue';
import { useRouter,useRoute } from 'vue-router';

export default defineComponent({
  name: 'Personal',
  setup(){
    const router = useRouter();
    const route = useRoute();
    // 记录当前选中的 `li` 索引
    const activeIndex = ref<number>(0);

    // 处理点击事件，切换 `active`
    const setActive = (index: number) => {
      activeIndex.value = index; // 让 Vue 负责管理 `active`
    };

    return {
      activeIndex,
      router,
      route,
      setActive,
    };
  }
});
</script>

<template>
  <router-view/>
  <div class = "shell">
    <ul class="nav">
        <li :class="{active: route.name === 'Home'}" @click ="setActive(0)" id = "avatar">
            <router-link :to="{name:'Home'}">
                <div class="icon">
                  <div class="imageBox">
                    <img src="@/assets/images/默认头像.jpg">
                  </div>
                </div>
                <div class="text">测试样例</div>
            </router-link>
        </li>
        <li :class="{active:route.name === 'Manager'}" @click="setActive(1)">
          <router-link :to="{name:'Manager'}">
            <div class="icon">
              <div class="imageBox">
              <img src="@/assets/images/个人.png">
              </div>
            </div>
            <div class="text">账号管理</div>
          </router-link>
        </li>
        <li :class="{active:route.name === 'AiManger'}" @click="setActive(2)">
          <router-link :to="{name:'Manager'}">
            <div class="icon">
              <div class="imageBox">
              <img src="@/assets/images/个人.png">
              </div>
            </div>
            <div class="text">测试样例2</div>
          </router-link>
        </li>
        <li :class="{active:route.name === 'AiManager'}" @click="setActive(3)">
          <router-link :to="{name:'Manager'}">
            <div class="icon">
              <div class="imageBox">
              <img src="@/assets/images/个人.png">
              </div>
            </div>
            <div class="text">测试样例3</div>
          </router-link>
        </li>
        <li :class="{active:route.name === 'AiManager'}" @click="setActive(4)">
          <router-link :to="{name:'Manager'}">
            <div class="icon">
              <div class="imageBox">
              <img src="@/assets/images/个人.png">
              </div>
            </div>
            <div class="text">测试样例4</div>
          </router-link>
        </li>
    </ul>
  </div>
  
</template>

<style scoped> 
  *{
    margin:0;
    padding:0;
    box-sizing : border-box;
    list-style:none;
    text-decoration:none;
  }

  section{
    position:relative;
    width:100%;
    height:100vh;
    display:flex;
    justify-content:center;
    align-items:center;
    font:900 100px '';
    color:rgba(175, 90, 240, 0.308);
    background-color: #e4e9f5;
  }

  .shell{
    position:fixed;
    top:0%;
    left:0%;
    width:84px;
    height:100%;
    background-color:#ead1fb;
    z-index:9999;
    transition:width 0.5s;
    padding-left:10px;
    overflow:hidden;
  }

  .shell:hover{
    width:300px;
  }

  .imageBox{
    position:relative;
    width:50px;
    height:50px;
    border-radius:50%;
    overflow:hidden;
  }

  .imageBox img{
    width:100%;
    height:100%;
    object-fit:cover;
  }

  .shell ul{
    position:relative;
    height:100vh;
  }

  .shell ul li{
    position:relative;
    padding:7px;
  }

  .active{
    background-color: #fff;
    border-top-left-radius: 50px;
    border-bottom-left-radius: 50px;
  }

  .active::before{
    content:"";
    position:absolute;
    top:-30px;
    right:0;
    width:30px;
    height:30px;
    border-bottom-right-radius:25px;
    box-shadow:5px 5px 0 5px #fff;
    background:transparent;
  }

  .active::after{
    content:"";
    position:absolute;
    bottom:-30px;
    right:0;
    width:30px;
    height:30px;
    border-top-right-radius: 25px;
    box-shadow:5px -5px 0 5px #fff;
    background:transparent;
  }

  #avatar{
    margin:100px 0 100px 0;
  }

  .shell ul li a{
    position:relative;
    display:flex;
    white-space:nowrap;
  }

  .icon{
    position:relative;
    display:flex;
    justify-content:center;
    align-items: center;
    min-width:60px;
    padding-left:10px;
    height:70px;
    color:#333;
    transition:0.5s;
    color: rgb(153, 109, 240)
  }

  .icon i{
    font-size:30px;
    z-index:999;
  }

  .text{
    position:relative;
    height:70px;
    display:flex;
    align-items:center;
    font-size:20px;
    color:#333; /*字体颜色 */
    padding-left:15px;
    text-transform:uppercase;
    letter-spacing:2px;/*字体间距*/
    transition:0.5s;
  }

  .shell ul li:hover a .icon,
  .shell ul li:hover a .text
  {
    color: #f3e7e9;/*字体和图标被选中后的颜色 */
  }

  .active a .icon::before{
    content:"";
    position:absolute;
    inset:5px;
    width:60px;
    background:#fff;
    border-radius:50%;
    transition:0.5s;
    border:7px solid rgb(110,90,240);
    box-sizing:border-box;
  }
</style>