<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="questions"
      :search="search"
      :sort-by="['creationDate']"
      sort-desc
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
            data-cy="searchBox"
          />

        </v-card-title>
      </template>

      <template v-slot:item.title="{ item }">
        <p
        >
          {{ item.title }}
        </p>
      </template>

  

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
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
              large
              class="mr-2"
              v-on="on"
              data-cy="clarificationRequests"
              @click="showClarificationRequests(item)"
              >live_help</v-icon
            >
          </template>
          <span>Clarification Requests</span>
        </v-tooltip>



      </template>
    </v-data-table>
    <footer>
      <v-icon class="mr-2">mouse</v-icon>Left-click on question's title to view
      it. <v-icon class="mr-2">mouse</v-icon>Right-click on question's title to
      edit it.
    </footer>

    <show-question-dialog
      v-if="currentQuestion"
      v-model="questionDialog"
      :question="currentQuestion"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />
    <public-clarification-requests-dialog
        v-if="!!currentQuestion"
      v-model="clarificationRequestsDialgog"
      :question="this.currentQuestion"
      v-on:close-public-clarification-requests-dialog="onClosePublicClarificationRequestsDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import ShowPublicClarificationRequestsDialog from '@/views/student/ShowPublicClarificationRequestsDialog.vue';
import Clarification from '@/models/discussion/Clarification';

@Component({
  components: {
    'public-clarification-requests-dialog': ShowPublicClarificationRequestsDialog,
    'show-question-dialog': ShowQuestionDialog
  }
})
export default class QuestionsView extends Vue {
  questions: Question[] = [];
  topics: Topic[] = [];
  currentQuestion: Question | null = null;
  clarificationRequestsDialgog : boolean = false;
    questionDialog: boolean = false;
  search: string = '';

  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '15%',
      sortable: false
    },
    { text: 'Title', value: 'title', align: 'left' },
    { text: 'Content', value: 'content', align: 'left' },
  ];


  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.questions] = await Promise.all([
        RemoteServices.getAnsweredQuestions()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
    console.log(this.questions);
  }

  customFilter(value: string, search: string, question: Question) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(question)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

    onCloseShowQuestionDialog() {
        this.currentQuestion = null;
        this.questionDialog = false;
    }

  showQuestionDialog(question: Question) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  showClarificationRequests(question: Question){
      console.log('xpto');
      this.currentQuestion = question;
      this.clarificationRequestsDialgog = true;
  }

  onClosePublicClarificationRequestsDialog(){
      this.clarificationRequestsDialgog = false;
      console.log('xpto');
  }


}

</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>
