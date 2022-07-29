<template>
    <div ref="parent" class="gamemap">
        <canvas ref="canvas">

        </canvas>
    </div>
</template>

<script>
import { GameMap } from "@/assets/scripts/GameMap"
import { ref, onMounted } from "vue"

export default{
    setup(){
        let parent = ref(null); // get the parent tag of canvas tag
        let canvas = ref(null); // get the canvas tag

        // after all components loaded, create a GameMap Object
        onMounted(() =>{
            new GameMap(canvas.value.getContext('2d'), parent.value)
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
