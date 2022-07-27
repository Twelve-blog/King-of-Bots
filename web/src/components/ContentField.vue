<template>
    <div class="containter content-feild">
        <div class="card">
            <div class="card-body">
                <!-- 公共标签，内容渲染到slot里面 -->
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<script>
</script>

<style scoped>
div.content-feild{
    margin-top:20px;
    /* margin-left: 40px;
    margin-right: 40px; */
    width: 80%;
    margin: 20px auto ;
}
</style>