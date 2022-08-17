<template>
    <ContentFiled>
        我的bot
    </ContentFiled>
</template>

<script>
import ContentFiled from "@/components/ContentField.vue"
import $ from "jquery"
import { useStore } from 'vuex'

export default {
    components: {
        ContentFiled
    },
    setup() {
        const store = useStore();
        $.ajax({
            url: "http://127.0.0.1:3000/user/bot/getlist/",
            type: 'get',
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                console.log(resp);
            },
            error(resp) {
                console.log(resp);
            }
        })
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/remove/",
        //     type: 'post',
        //     data: {
        //         bot_id: 2,
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })
    }
}
</script>

<style scoped>

</style>