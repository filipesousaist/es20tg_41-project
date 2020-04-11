<template>
    <div class="container">
        <h2>Create Student Question</h2>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import RemoteServices from "@/services/RemoteServices";

    @Component
    export default class CreateStudentQuestionView extends Vue {

        async created() {
            await this.$store.dispatch('loading');
            try {
                //this.studentQuestions = (await RemoteServices.getStudentQuestions()).reverse();
            } catch (error) {
                await this.$store.dispatch('error', error);
            }
            await this.$store.dispatch('clearLoading');
        }
    }
</script>

<style scoped>

</style>