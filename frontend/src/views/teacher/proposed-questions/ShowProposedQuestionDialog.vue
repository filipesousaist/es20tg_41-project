<template>
  <v-dialog
    v-model="dialog"
    @keydown.esc="closeProposedQuestionDialog"
    max-width="75%"
    persistent
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ studentQuestion.questionDto.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-proposed-question :studentQuestion="studentQuestion" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeProposedQuestionDialog"
        >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
  import { Component, Vue, Prop } from 'vue-property-decorator';
  import StudentQuestion from '@/models/student_question/StudentQuestion';
  import ShowStudentQuestion from '@/views/student/ShowStudentQuestion.vue';
  import ShowProposedQuestion from './ShowProposedQuestion.vue';

  @Component({
    components: {
      'show-proposed-question': ShowProposedQuestion
    }
  })
  export default class ShowProposedQuestionDialog extends Vue {
    @Prop({ type: StudentQuestion, required: true })
    readonly studentQuestion!: StudentQuestion;
    @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

    closeProposedQuestionDialog() {
      this.$emit('close-show-proposed-question-dialog');
    }
  }
</script>
