<template>
  <div class="container">
    <h2>Available Tournaments in {{ course }}</h2>
    <ul>
      <li class="list-header">
        <div class="col">Name</div>
        <div class="col">Topics</div>
        <div class="col">Available since</div>
        <div class="col">Available until</div>
        <div class="col">Questions</div>
        <div class="col">Participants</div>
        <div class="col last-col"></div>
      </li>
      <li
        class="list-row"
        v-for="tournament in tournaments"
        :key="tournament.id"
      >
        <div class="col">
          {{ tournament.name }}
        </div>
        <div class="col">
          <div
            v-for="topic in tournament.topics"
            :key="topic.id"
            style="padding-bottom:2px;padding-top: 2px;"
          >
            <v-chip>{{ topic.name }}</v-chip>
          </div>
        </div>
        <div class="col">
          {{ tournament.beginningTime }}
        </div>
        <div class="col">
          {{ tournament.endingTime }}
        </div>
        <div class="col">
          {{ tournament.numberOfQuestions }}
        </div>
        <div class="col">
          {{ tournament.studentsUsername.length }}
        </div>
        <div class="col last-col">
          <li v-if="!tournament.studentsUsername.includes(username)">
            <v-btn
              color="primary"
              @click="enrollTournament(tournament)"
              data-cy="enroll"
            >
              Enroll
            </v-btn>
          </li>
          <li v-else>
            <v-btn
              color="error"
              @click="enrollTournament(tournament)"
              data-cy="withdraw"
            >
              Withdraw
            </v-btn>
          </li>
          <li v-if="tournament.creatorName === username">
            <v-btn
              color="error"
              @click="deleteTournament(tournament)"
              data-cy="delete"
            >
              Delete
            </v-btn>
          </li>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Tournament from '@/models/tournament/Tournament';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EnrollTournament extends Vue {
  course = this.$store.getters.getCurrentCourse.name;
  username = this.$store.getters.getUser.username;
  tournaments: Tournament[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (
        await RemoteServices.getAllOpenTournament()
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async enrollTournament(tournament: Tournament) {
    await this.$store.dispatch('loading');
    try {
      if (tournament.studentsUsername.includes(this.username)) {
        await RemoteServices.unenrollTournament(tournament.id);
        tournament.studentsUsername.splice(tournament.studentsUsername.indexOf(this.username), 1);
      } else {
        await RemoteServices.enrollTournament(tournament.id);
        tournament.studentsUsername.push(this.username);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async deleteTournament(tournament: Tournament) {
    await this.$store.dispatch('loading');
    try {
      if (tournament.creatorName == this.username) {
        await RemoteServices.deleteTournament(tournament.id);
        await this.$router.push('showTournaments');
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
<style lang="scss" scoped>
.container {
  max-width: 1250px;
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
