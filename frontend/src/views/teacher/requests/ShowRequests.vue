<template>
  <div>
    <show-question question="question" />

  </div>
</template>








<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import ShowQuestion from '@/views/teacher/questions/ShowQuestion.vue';

@Component({
  components: {
    'show-question': ShowQuestion
  }
})
export default class ShowRequests extends Vue {
  @Prop({ type: Question, required: true }) readonly question!: Question;
}


</script>
