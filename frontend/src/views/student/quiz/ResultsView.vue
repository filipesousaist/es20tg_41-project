<template>
  <div class="quiz-container" v-if="statementManager.correctAnswers.length > 0">
    <div class="question-navigation">
      <div class="navigation-buttons">
        <span
          v-for="index in +statementManager.statementQuiz.questions.length"
          v-bind:class="[
            'question-button',
            index === questionOrder + 1 ? 'current-question-button' : '',
            index === questionOrder + 1 &&
            statementManager.correctAnswers[index - 1].correctOptionId !==
              statementManager.statementQuiz.answers[index - 1].optionId
              ? 'incorrect-current'
              : '',
            statementManager.correctAnswers[index - 1].correctOptionId !==
            statementManager.statementQuiz.answers[index - 1].optionId
              ? 'incorrect'
              : ''
          ]"
          :key="index"
          @click="changeOrder(index - 1)"
        >
          {{ index }}
        </span>
      </div>
      <span
        class="left-button"
        @click="decreaseOrder"
        v-if="questionOrder !== 0"
        ><i class="fas fa-chevron-left"
      /></span>
      <span
        class="right-button" data-cy="nextQuestionButton"
        @click="increaseOrder"
        v-if="
          questionOrder !== statementManager.statementQuiz.questions.length - 1
        "
        ><i class="fas fa-chevron-right"
      /></span>
    </div>

     


    <result-component
      v-model="questionOrder"
      :answer="statementManager.statementQuiz.answers[questionOrder]"
      :correctAnswer="statementManager.correctAnswers[questionOrder]"
      :question="statementManager.statementQuiz.questions[questionOrder]"
      :questionNumber="statementManager.statementQuiz.questions.length"
      @increase-order="increaseOrder"
      @decrease-order="decreaseOrder"
    />

    <clarification-request-dialog
      v-if="clarificationRequest"
      v-model="clarificationRequestDialog"
      :question="statementManager.statementQuiz.questions[questionOrder]"
      v-on:close-dialog="onCloseDialog"
    />

    <view-clarification-request-dialog
      v-model="viewClarificationRequestDialog"
      :clarificationRequest ="statementManager.statementQuiz.questions[questionOrder].clarificationRequest"
      :clarification="currClarification"
      v-on:close-view-clarreq-dialog="onCloseViewClarReqDialog"
    />

    <v-btn color="primary" dark @click="createClarificationRequest" v-if="!existsClarificationRequest" data-cy="requestButton"
            >Request Clarification</v-btn>
    
    <v-btn color="primary" dark @click="viewClarificationRequest" v-if="existsClarificationRequest"
            >View Clarification Request</v-btn>

  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import ResultComponent from '@/views/student/quiz/ResultComponent.vue';
import ClarificationRequestDialog from '@/views/student/quiz/ClarificationRequestDialog.vue';
import ViewClarificationRequestDialog from '@views/student/quiz/ViewClarificationRequestDialog.vue';
import ClarificationRequest from '@/models/discussion/ClarificationRequest';
import Clarification from '@/models/discussion/Clarification';
import RemoteServices from '@/services/RemoteServices';


@Component({
  components: {
    'result-component': ResultComponent,
    'clarification-request-dialog': ClarificationRequestDialog,
    'view-clarification-request-dialog': ViewClarificationRequestDialog
  }
})
export default class ResultsView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  questionOrder: number = 0;
  existsClarificationRequest: boolean = !!this.statementManager.statementQuiz!.questions[this.questionOrder].clarificationRequest;
  clarificationRequestDialog: boolean = false;
  viewClarificationRequestDialog: boolean = false;
  clarificationRequest: ClarificationRequest | null = null;
  currClarification: Clarification | null = null;

  async created() {
    if (this.statementManager.isEmpty()) {
      await this.$router.push({ name: 'create-quiz' });
    } else if (this.statementManager.correctAnswers.length === 0) {
      await this.$store.dispatch('loading');
      setTimeout(() => {
        this.statementManager.concludeQuiz();
      }, 2000);

      await this.$store.dispatch('clearLoading');
    }
  }

  increaseOrder(): void {
    if (
      this.questionOrder + 1 <
      +this.statementManager.statementQuiz!.questions.length
    ) {
      this.questionOrder += 1;
    }
    this.existsClarificationRequest = !!this.statementManager.statementQuiz!.questions[this.questionOrder].clarificationRequest;
  }

  decreaseOrder(): void {
    if (this.questionOrder > 0) {
      this.questionOrder -= 1;
    }
    this.existsClarificationRequest = !!this.statementManager.statementQuiz!.questions[this.questionOrder].clarificationRequest;

  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.statementManager.statementQuiz!.questions.length) {
      this.questionOrder = n;
    }
    this.existsClarificationRequest = !!this.statementManager.statementQuiz!.questions[this.questionOrder].clarificationRequest;

  }

  createClarificationRequest(): void{
    this.clarificationRequestDialog = true;
    this.clarificationRequest = new ClarificationRequest();
  }
  
  async viewClarificationRequest(){
    let result;
    await this.$store.dispatch('loading');
    try {
      result = await RemoteServices.getClarification(this.statementManager.statementQuiz!.questions[this.questionOrder].clarificationRequest.id);
    } catch (error) {
    }finally{
    await this.$store.dispatch('clearLoading');

    if(!!result) 
      this.currClarification = result;
    else
    this.currClarification = null;
    this.viewClarificationRequestDialog = true;
    }
  }

  onCloseDialog(): void{
    console.log('yikes');
    this.clarificationRequestDialog = false;
    this.clarificationRequest = null;
    this.existsClarificationRequest = !!this.statementManager.statementQuiz!.questions[this.questionOrder].clarificationRequest;

  }

  onCloseViewClarReqDialog(): void{
    this.viewClarificationRequestDialog = false;
    this.currClarification = null;
  }
}
</script>

<style lang="scss" scoped>
.incorrect {
  color: #cf2323 !important;
}

.incorrect-current {
  background-color: #cf2323 !important;
  color: #fff !important;
}
</style>
