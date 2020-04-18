<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="studentQuestions"
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

      <template v-slot:item.image="{ item }">
        <v-file-input
          show-size
          dense
          small-chips
          @change="handleFileUpload($event, item)"
          accept="image/*"
        />
      </template>
    </v-data-table>

    <edit-student-question-dialog
      v-if="currentStudentQuestion"
      v-model="editStudentQuestionDialog"
      :studentQuestion="currentStudentQuestion"
      v-on:save-student-question="onSaveStudentQuestion"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/management/StudentQuestion';
import EditStudentQuestionDialog from '@/views/student/EditStudentQuestionDialog.vue';
import Image from '@/models/management/Image';


@Component({
  components: {
    'edit-student-question-dialog': EditStudentQuestionDialog
  }
})
export default class CreateStudentQuestionsView extends Vue {
  studentQuestions: StudentQuestion[] = [];
  currentStudentQuestion: StudentQuestion | null = null;
  editStudentQuestionDialog: boolean = false;
  search: string = '';
  headers: object = [
    { text: 'Title', value: 'questionDto.title', align: 'left', width: '30%' },
    {
      text: 'Status',
      value: 'questionDto.status',
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
      align: 'right',
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

  newStudentQuestion() {
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

  onCloseDialog() {
    this.editStudentQuestionDialog = false;
    this.currentStudentQuestion = null;
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
