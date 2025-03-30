<script lang="ts">
import Personal from './Personal/Personal.vue'
import { defineComponent } from "vue";
export default defineComponent({
    name:'PersonalLayout',
    components:{
        Personal
    }
});

</script>

<template>
    <div>
        <Personal/>
        <div>
            <router-view/>
        </div>
    </div>
</template>

<style scoped>

</style>