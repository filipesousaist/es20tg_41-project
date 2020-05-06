<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Evaluate Student Question</span>
      </v-card-title>

      <v-card-text class="text-left" v-if="questionEvaluation">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-radio-group v-model="questionEvaluation.approved" row>
              <v-radio
                label="Approve Question"
                v-bind:value="true"
                data-cy="approveRadioButton"
              ></v-radio>
              <v-radio
                label="Reject Question"
                v-bind:value="false"
                data-cy="rejectRadioButton"
              ></v-radio>
            </v-radio-group>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="questionEvaluation.justification"
                label="Justification"
                data-cy="justificationTextField"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('dialog', false)"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="submitEvaluation"
          data-cy="submitButton"
          >Submit</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/student_question/StudentQuestion';
import QuestionEvaluation from '@/models/student_question/QuestionEvaluation';

@Component
export default class EvaluateQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly studentQuestion!: StudentQuestion;

  questionEvaluation!: QuestionEvaluation;

  created() {
    this.questionEvaluation = new QuestionEvaluation();
  }

  async submitEvaluation() {
    if (this.questionEvaluation) {
      if (this.questionEvaluation.approved === null) {
        await this.$store.dispatch(
          'error',
          'Error: You must select whether to approve or not the student question'
        );
        return;
      } else if (!this.questionEvaluation.justification) {
        await this.$store.dispatch(
          'error',
          'Error: Question evaluation must have a justification'
        );
        return;
      } else
        try {
          const result = await RemoteServices.submitEvaluation(
            this.questionEvaluation,
            this.studentQuestion.id
          );
          this.$emit('submit-evaluation', result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
    }
  }
}
</script>

<style scoped></style>
