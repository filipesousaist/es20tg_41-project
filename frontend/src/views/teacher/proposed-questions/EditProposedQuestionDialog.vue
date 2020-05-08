<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
    persistent
  >
    <v-card max-height="30%">
      <v-card-title>
        <span class="headline">
          Edit Student Question
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editStudentQuestion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="editStudentQuestion.questionDto.title"
                label="Title"
                data-cy="Title"
              />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="5"
                v-model="editStudentQuestion.questionDto.content"
                label="Question"
                data-cy="Question"
              ></v-textarea>
            </v-flex>
            <v-flex
              xs24
              sm12
              md12
              v-for="index in editStudentQuestion.questionDto.options.length"
              :key="index"
            >
              <v-switch
                v-model="
                  editStudentQuestion.questionDto.options[index - 1].correct
                "
                class="ma-4"
                label="Correct"
                data-cy="Correct"
              />
              <v-textarea
                outline
                rows="5"
                v-model="
                  editStudentQuestion.questionDto.options[index - 1].content
                "
                label="Content"
                data-cy="Content"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="saveStudentQuestion"
          data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import StudentQuestion from '@/models/student_question/StudentQuestion';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EditProposedQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly studentQuestion!: StudentQuestion;

  editStudentQuestion!: StudentQuestion;

  created() {
    this.editStudentQuestion = new StudentQuestion(this.studentQuestion);
  }

  async saveStudentQuestion() {
    if (this.editStudentQuestion && !this.editStudentQuestion.questionDto) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    if (this.editStudentQuestion) {
      try {
        const result = await RemoteServices.updateStudentQuestion(
          this.editStudentQuestion
        );
        this.$emit('save-student-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped></style>
