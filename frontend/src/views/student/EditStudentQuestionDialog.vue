<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="50%"
    max-height="50%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          New Student Question
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
                rows="10"
                v-model="editStudentQuestion.questionDto.content"
                label="Content"
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
                rows="10"
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
        <v-btn color="blue darken-1" @click="$emit('close-dialog')" data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveStudentQuestion" data-cy="saveButton">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import StudentQuestion from '@/models/management/StudentQuestion';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';

@Component
export default class EditStudentQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly studentQuestion!: StudentQuestion;

  editStudentQuestion!: StudentQuestion;

  created() {
    this.editStudentQuestion = new StudentQuestion(this.studentQuestion);
  }

  async saveStudentQuestion() {
    console.log(this.editStudentQuestion.questionDto.title);

    this.editStudentQuestion.ser = 123;
    if (this.editStudentQuestion && !this.editStudentQuestion.questionDto) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    if (this.editStudentQuestion) {
      try {
        const result = await RemoteServices.createStudentQuestion(
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
