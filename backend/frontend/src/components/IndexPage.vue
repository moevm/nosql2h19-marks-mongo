<template>
    <div>
        <router-view>

        </router-view>
        <div class="wrapper m-5 justify-content-center">
            <h1 class="text-center">
                Project "Marks" on the subject "Non-Relational databases"
            </h1>
        </div>
        <div class="wrapper justify-content-center">
            <p class="text-center">Vergeichik Gleb</p>
        </div>
        <div class="wrapper justify-content-center">
            <p class="text-center">Veroha Veronika</p>
        </div>
        <div class="wrapper justify-content-center">
            <p class="text-center">Shiryaev Yaroslav</p>
        </div>
    </div>
</template>

<script>
    export default {
        name: "IndexPage"
    }
</script>

<style scoped>

    .text-center {
        text-align: center;
    }

</style>