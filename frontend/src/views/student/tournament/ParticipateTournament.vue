<template>
  <div class="container">
    <h2>Available Tournament Quizzes in {{ course }}</h2>
    <ul>
      <li class="list-header">
        <div class="col">Tournament Name</div>
        <div class="col">Available since</div>
        <div class="col">Available until</div>
        <div class="col">Number of questions</div>
        <div class="col last-col"></div>
      </li>
      <li class="list-row" v-for="quiz in quizzes" :key="quiz.quizAnswerId">
        <div class="col">
          {{ quiz.title }}
        </div>
        <div class="col">
          {{ quiz.availableDate }}
        </div>
        <div class="col">
          {{ quiz.conclusionDate }}
        </div>
        <div class="col">
          {{ quiz.questions.length }}
        </div>
        <div class="col last-col">
          <v-btn color="primary" @click="solveQuiz(quiz)" data-cy="Answer">
            Answer
          </v-btn>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatementManager from '@/models/statement/StatementManager';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';

@Component
export default class ParticipateTournament extends Vue {
  course = this.$store.getters.getCurrentCourse.name;
  quizzes: StatementQuiz[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.quizzes = (
        await RemoteServices.getAvailableTournamentQuizzes()
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async solveQuiz(quiz: StatementQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.statementQuiz = quiz;
    await this.$router.push({ name: 'solve-quiz' });
  }
}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }

  ul {
    overflow: hidden;
    padding: 0 5px;

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .col {
      flex-basis: 25% !important;
      margin: auto; /* Important */
      text-align: center;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>
