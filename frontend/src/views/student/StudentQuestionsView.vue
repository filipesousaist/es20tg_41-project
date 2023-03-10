<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="studentQuestions"
      :custom-filter="customFilter"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
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
            data-cy="searchBar"
          />
          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="newStudentQuestion"
            data-cy="createButton"
            >New Student Question</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item.status="{ item }">
        <v-chip :color="getStatusColor(item.status)" medium>
          <span>{{ item.status }}</span>
        </v-chip>
      </template>

      <template v-slot:item.questionDto.image="{ item }">
        <v-file-input
          show-size
          dense
          small-chips
          @change="handleFileUpload($event, item)"
          accept="image/*"
        />
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom v-if="isRejected(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="editQuestion(item)"
              data-cy="editButton"
              >edit</v-icon
            >
          </template>
          <span>Edit Question</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showStudentQuestionDialog(item)"
              data-cy="showStudentQuestionButton"
              >visibility</v-icon
            >
          </template>
          <span>Show Question</span>
        </v-tooltip>
      </template>
    </v-data-table>

    <edit-student-question-dialog
      v-if="currentStudentQuestion && !studentQuestionDialog"
      v-model="editStudentQuestionDialog"
      :studentQuestion="currentStudentQuestion"
      v-on:save-student-question="onSaveStudentQuestion"
      v-on:close-dialog="onCloseDialog"
    />

    <show-student-question-dialog
      v-if="currentStudentQuestion && !editStudentQuestionDialog"
      :dialog="studentQuestionDialog"
      :studentQuestion="currentStudentQuestion"
      v-on:close-show-student-question-dialog="onCloseShowStudentQuestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/student_question/StudentQuestion';
import EditStudentQuestionDialog from '@/views/student/EditStudentQuestionDialog.vue';
import Image from '@/models/management/Image';
import ShowStudentQuestionDialog from '@/views/student/ShowStudentQuestionDialog.vue';

@Component({
  components: {
    'edit-student-question-dialog': EditStudentQuestionDialog,
    'show-student-question-dialog': ShowStudentQuestionDialog
  }
})
export default class CreateStudentQuestionsView extends Vue {
  studentQuestions: StudentQuestion[] = [];
  currentStudentQuestion: StudentQuestion | null = null;
  editStudentQuestionDialog: boolean = false;
  studentQuestionDialog: boolean = false;
  search: string = '';
  headers: object = [
    { text: 'Title', value: 'questionDto.title', align: 'left', width: '30%' },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Creation Date',
      value: 'questionDto.creationDate',
      align: 'center'
    },
    {
      text: 'Image',
      value: 'questionDto.image',
      align: 'center',
      sortable: false
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];

  @Watch('editStudentQuestionDialog')
  closeError() {
    if (!this.editStudentQuestionDialog) {
      this.currentStudentQuestion = null;
    }
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.studentQuestions = await RemoteServices.getStudentQuestions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  customFilter(
    value: string,
    search: string,
    studentQuestion: StudentQuestion
  ) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(studentQuestion)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  isRejected(studentQuestion: StudentQuestion | null) {
    return studentQuestion?.status == 'REJECTED';
  }

  editQuestion(studentQuestion: StudentQuestion) {
    this.currentStudentQuestion = studentQuestion;
    this.editStudentQuestionDialog = true;
  }

  newStudentQuestion() {
    this.studentQuestionDialog = false;
    this.currentStudentQuestion = new StudentQuestion();
    this.editStudentQuestionDialog = true;
  }

  async onSaveStudentQuestion(studentQuestion: StudentQuestion) {
    this.studentQuestions = this.studentQuestions.filter(
      q => q.id !== studentQuestion.id
    );
    this.studentQuestions.unshift(studentQuestion);
    this.editStudentQuestionDialog = false;
    this.currentStudentQuestion = null;
  }

  async handleFileUpload(event: File, studentQuestion: StudentQuestion) {
    if (studentQuestion.questionDto.id) {
      try {
        const imageURL = await RemoteServices.uploadImage(
          event,
          studentQuestion.questionDto.id
        );
        studentQuestion.questionDto.image = new Image();
        studentQuestion.questionDto.image.url = imageURL;
        confirm('Image ' + imageURL + ' was uploaded!');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  showStudentQuestionDialog(studentQuestion: StudentQuestion) {
    this.editStudentQuestionDialog = false;
    this.currentStudentQuestion = studentQuestion;
    this.studentQuestionDialog = true;
  }

  onCloseShowStudentQuestionDialog() {
    this.studentQuestionDialog = false;
    this.currentStudentQuestion = null;
  }

  onCloseDialog() {
    this.editStudentQuestionDialog = false;
    this.currentStudentQuestion = null;
  }

  getStatusColor(status: string) {
    switch (status) {
      case 'PROPOSED':
        return 'blue';
      case 'ACCEPTED':
        return 'green';
      case 'REJECTED':
        return 'red';
    }
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
