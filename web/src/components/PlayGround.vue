<template>
    <div class="playground">
        <!-- <BeginBoard /> -->
        <GameMap />
    </div>
</template>

<script>
import GameMap from "./GameMap"
//import BeginBoard from "./BeginBoard"

export default{
    components: {
        GameMap,
        //BeginBoard,
    }
}
</script>

<style scoped>
div.playground{
    width: 60vw;
    height: 70vh;
    background: lightblue;
    margin: 40px auto;
}
</style>