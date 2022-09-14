<template>
    <ContentField>
        <div class="title">
            Welcome to the world of King of Bots!
        </div>
        <div class="context">
            Welcome! New trainer! In this world, you will try to create and manipulate your own bots to fight with others!
            Let me introduce some significant rules in this world to you!
        </div>
        <div class="context">
            First of all, you need to create a your own bots. 
        </div>
        <div class="context">
            After having your own bots, you can manipulate them to fight with others' bots.
            <img src="@/assets/images/directions.png" alt="direction operation" style="width: 50%; margin-left: 25%">
        </div>
    </ContentField>
</template>

<script>
import ContentField from "@/components/ContentField"

export default{
    components: {
        ContentField,
    }
}

</script>

<style scoped>
    .title{
        text-align: center;
        font-size: 5vh;
    }
    .context{
        margin-top: 2vh;
        font-size: 3vh;
        text-indent: 2em;
    }
</style>