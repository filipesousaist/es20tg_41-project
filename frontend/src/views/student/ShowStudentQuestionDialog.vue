<template>
  <v-dialog
    v-model="dialog"
    @keydown.esc="closeStudentQuestionDialog"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ studentQuestion.questionDto.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-student-question :studentQuestion="studentQuestion" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeStudentQuestionDialog"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import StudentQuestion from '@/models/management/StudentQuestion';
import ShowStudentQuestion from '@/views/student/ShowStudentQuestion.vue';

@Component({
  components: {
    'show-student-question': ShowStudentQuestion
  }
})
export default class ShowStudentQuestionDialog extends Vue {
  @Prop({ type: StudentQuestion, required: true })
  readonly studentQuestion!: StudentQuestion;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  closeStudentQuestionDialog() {
    this.$emit('close-show-student-question-dialog');
  }
}
</script>
