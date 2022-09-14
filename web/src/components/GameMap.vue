<template>
    <!-- Use the ref attribute to map the label to the corresponding parameter -->
    <div ref="parent" class="gamemap"> 
        <!-- tabindex attribute: get the imput of keyboard -->
        <canvas ref="canvas" tabindex="0">

        </canvas>
    </div>
</template>

<script>
import { GameMap } from "@/assets/scripts/GameMap"
import { ref, onMounted } from "vue"
import {useStore} from 'vuex'

export default{
    setup(){
        let parent = ref(null); // get the parent tag of canvas tag
        let canvas = ref(null); // get the canvas tag
        const store = useStore();

        // after all components loaded, create a GameMap Object
        onMounted(() =>{
            new GameMap(canvas.value.getContext('2d'), parent.value, store)
        })

        return {
            parent,
            canvas,
        }
    }
}
</script>

<style scoped>
    div.gamemap{
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
    }
</style>
