<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="studentQuestions"
      :search="search"
      multi-sort
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
          />
        </v-card-title>
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="evaluateQuestion(item)"
            >
              edit
            </v-icon>
          </template>
          <span>Evaluate Question</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <evaluate-question-dialog
      v-if="currentStudentQuestion"
      v-model="evaluateQuestionDialog"
      :studentQuestion="currentStudentQuestion"
      v-on:submit-evaluation="onSubmitEvaluation"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import StudentQuestion from '@/models/management/StudentQuestion';
import RemoteServices from '@/services/RemoteServices';
import EvaluateQuestionDialog from '@/views/teacher/proposed-questions/EvaluateQuestionDialog.vue';

@Component({
  components: {
    'evaluate-question-dialog': EvaluateQuestionDialog
  }
})
export default class ProposedQuestionsView extends Vue {
  studentQuestions: StudentQuestion[] = [];
  search: string = '';
  evaluateQuestionDialog: boolean = false;
  currentStudentQuestion: StudentQuestion | null = null;
  headers: object = [
    { text: 'Title', value: 'questionDto.title', align: 'center' },
    {
      text: 'Status',
      value: 'questionDto.status',
      align: 'center'
    },
    {
      text: 'Creation Date',
      value: 'questionDto.creationDate',
      align: 'center'
    },
    {
      text: 'Image',
      value: 'questionDto.image',
      align: 'right',
      sortable: false
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
      this.studentQuestions = await RemoteServices.getProposedStudentQuestions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  @Watch('evaluateQuestionDialog')
  closeError() {
    if (!this.evaluateQuestionDialog) {
      this.currentStudentQuestion = null;
    }
  }

  evaluateQuestion(studentQuestion: StudentQuestion) {
    this.currentStudentQuestion = studentQuestion;
    this.evaluateQuestionDialog = true;
  }

  onSubmitEvaluation() {
    this.evaluateQuestionDialog = false;
  }
}
</script>

<style lang="scss" scoped></style>
