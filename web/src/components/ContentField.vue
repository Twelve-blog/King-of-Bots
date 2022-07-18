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
}
</style>