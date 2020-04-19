<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="requests"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      multi-sort
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
        </v-card-title>
      </template>

      <template v-slot:item.question.content="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.content, null)"
          @click="showQuestionDialog(item)"
      /></template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
        <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showQuestionDialog(item)"
              >visibility</v-icon
            >
          </template>
          <span>Show Question</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showCreateClarificationDialog(item)"
              >edit</v-icon>
          </template>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ClarificationRequest from '@/models/discussion/ClarificationRequest';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import ClarificationRequestsDialog from '@/views/teacher/questions/ShowClarificationRequestsDialog.vue';
import Image from '@/models/management/Image';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'show-question-dialog': ShowQuestionDialog,
    'create-clarification-dialog': ClarificationRequestsDialog
  }
})
export default class ClarificationRequests extends Vue {
  requests: ClarificationRequest[] = [];
  questions: Question[] = [];
  search: string = '';
  currentRequest: ClarificationRequest | null = null;
  currentQuestion: Question | null = null;
  requestDialog: boolean = false;
  createRequestDialog: boolean = false;
  questionDialog: boolean = false;
  headers: object = [
    { text: 'Title', name: 'title', align: 'center' },
    { text: 'Clarification Request', name: 'text', align: 'left' },
    {
      text: 'Question Requested',
      name: 'question',
      align: 'left'
    },
    {
      text: 'Clarification Text',
      name: 'clarification',
      align: 'left'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.questions, this.requests] = await Promise.all([
        RemoteServices.getQuestions(),
        this.getRequests(this.questions)
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  getRequests(questions: Question[]) {
    let clarificationRequests = [];
    for (let question of questions) {
      for (let request of question.requests) {
        request.question = question;
        clarificationRequests.push(request);
      }
    }
    return clarificationRequests;
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  showQuestionDialog(question: Question) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.questionDialog = false;
  }

  showCreateClarificationDialog(request: ClarificationRequest) {
    this.currentRequest = request;
    this.createRequestDialog = true;
  }

  onCloseCreateClarificationDialog() {
    this.createRequestDialog = false;
  }
}
</script>



<!--
clarificationRequestDialog(question : Question) {
this.currentQuestion = question;
this.requestDialog = true;
}

onCloseClarificationRequestDialog() {
this.requestDialog = false;
}-->

<style lang="scss" scoped></style>