<script set lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'Personal',
});
</script>

<template>
  <div class = "shell">
    <ul class="nav">
        <li class="active" id = "avatar">
            <router-link to="/">
                <div class="imageBox">
                    
                </div>
            </router-link>
        </li>
    </ul>
  </div>
</template>

<style lang="scss" scoped> 

</style>