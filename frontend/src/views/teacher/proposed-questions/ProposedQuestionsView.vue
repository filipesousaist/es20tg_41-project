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
      data-cy="proposedQuestionsTable"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
            data-cy="searchBar"
          />
        </v-card-title>
      </template>
      <template v-slot:item.status="{ item }">
        <v-chip :color="getStatusColor(item.status)" medium>
          <span>{{ item.status }}</span>
        </v-chip>
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showProposedQuestionDialog(item)"
              data-cy="showProposedQuestionButton"
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
              @click="evaluateQuestion(item)"
              data-cy="evaluateButton"
            >
              book
            </v-icon>
          </template>
          <span>Evaluate Question</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isAccepted(item) && !isAvailable(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="makeAvailable(item)"
              data-cy="makeAvailableButton"
            >
              playlist_add_check
            </v-icon>
          </template>
          <span>Make Available</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isAccepted(item) && !isAvailable(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="editQuestion(item)"
              data-cy="editButton"
            >
              edit
            </v-icon>
          </template>
          <span>Edit Question</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <evaluate-question-dialog
      v-if="currentStudentQuestion"
      v-model="evaluateQuestionDialog"
      :studentQuestion="currentStudentQuestion"
      v-on:submit-approval="onSubmitEvaluation(true)"
      v-on:submit-refusal="onSubmitEvaluation(false)"
    />
    <edit-proposed-question-dialog
      v-if="currentStudentQuestion"
      v-model="editProposedQuestionDialog"
      :studentQuestion="currentStudentQuestion"
      v-on:save-student-question="onSaveStudentQuestion"
      v-on:close-dialog="onCloseDialog"
    />
    <show-proposed-question-dialog
      v-if="currentStudentQuestion"
      :dialog="proposedQuestionDialog"
      :studentQuestion="currentStudentQuestion"
      v-on:close-show-proposed-question-dialog="
        onCloseShowProposedQuestionDialog
      "
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import StudentQuestion from '@/models/student_question/StudentQuestion';
import RemoteServices from '@/services/RemoteServices';
import EvaluateQuestionDialog from '@/views/teacher/proposed-questions/EvaluateQuestionDialog.vue';
import EditProposedQuestionDialog from '@/views/teacher/proposed-questions/EditProposedQuestionDialog.vue';
import ShowProposedQuestionDialog from './ShowProposedQuestionDialog.vue';

@Component({
  components: {
    'evaluate-question-dialog': EvaluateQuestionDialog,
    'edit-proposed-question-dialog': EditProposedQuestionDialog,
    'show-proposed-question-dialog': ShowProposedQuestionDialog
  }
})
export default class ProposedQuestionsView extends Vue {
  studentQuestions: StudentQuestion[] = [];
  search: string = '';
  evaluateQuestionDialog: boolean = false;
  editProposedQuestionDialog: boolean = false;
  proposedQuestionDialog: boolean = false;
  currentStudentQuestion: StudentQuestion | null = null;
  headers: object = [
    { text: 'Title', value: 'questionDto.title', align: 'center' },
    {
      text: 'Status',
      value: 'status',
      align: 'center'
    },
    {
      text: 'Question Status',
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
      this.studentQuestions = await RemoteServices.getNonRejectedStudentQuestions();
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

  onSubmitEvaluation(approved: boolean) {
    if (this.currentStudentQuestion) {
      let currentSQ = this.currentStudentQuestion;

      if (approved) currentSQ.status = 'ACCEPTED';
      else
        this.studentQuestions = this.studentQuestions.filter(sq => {
          return sq.id != currentSQ.id;
        });
    }
    this.evaluateQuestionDialog = false;
  }

  isAccepted(studentQuestion: StudentQuestion | null) {
    return studentQuestion?.status == 'ACCEPTED';
  }

  isAvailable(studentQuestion: StudentQuestion | null) {
    return studentQuestion?.questionDto.status == 'AVAILABLE';
  }

  async makeAvailable(studentQuestion: StudentQuestion) {
    try {
      await RemoteServices.makeStudentQuestionAvailable(studentQuestion.id);
      studentQuestion.questionDto.status = 'AVAILABLE';
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  editQuestion(studentQuestion: StudentQuestion) {
    this.currentStudentQuestion = studentQuestion;
    this.editProposedQuestionDialog = true;
  }

  async onSaveStudentQuestion(studentQuestion: StudentQuestion) {
    this.studentQuestions = this.studentQuestions.filter(
      q => q.id !== studentQuestion.id
    );
    this.studentQuestions.unshift(studentQuestion);
    this.editProposedQuestionDialog = false;
    this.currentStudentQuestion = null;
  }

  onCloseDialog() {
    this.editProposedQuestionDialog = false;
    this.currentStudentQuestion = null;
  }

  showProposedQuestionDialog(studentQuestion: StudentQuestion) {
    this.currentStudentQuestion = studentQuestion;
    this.proposedQuestionDialog = true;
  }

  onCloseShowProposedQuestionDialog() {
    this.proposedQuestionDialog = false;
    this.currentStudentQuestion = null;
  }

  getStatusColor(status: string) {
    switch (status) {
      case 'PROPOSED':
        return 'blue';
      case 'ACCEPTED':
        return 'green';
    }
  }
}
</script>

<style lang="scss" scoped></style>
