<template>
  <v-card class="container" style="margin-top: 3%;">
    <v-card-title>
      <span class="headline">
        New Tournament on {{ $store.getters.getCurrentCourse.name }}
      </span>
    </v-card-title>

    <v-card-text class="text-left" v-if="currentTournament">
      <v-container grid-list-md fluid>
        <v-layout column wrap>
          <v-flex xs24 sm12 md8>
            <v-text-field
              v-model="currentTournament.name"
              label="Name of the tournament"
              data-cy="tournamentName"
            />
            <v-datetime-picker
              label="Select beginning"
              v-model="currentTournament.beginningTime"
              data-cy="Selectbeginning"
            >
            </v-datetime-picker>
            <v-datetime-picker class="text-left"
              label="Select ending"
              v-model="currentTournament.endingTime"
              data-cy="Select ending"
            >
            </v-datetime-picker>
            <v-text-field
              v-model="currentTournament.numberOfQuestions"
              label="Number of questions (Maximum of 25)"
              data-cy="Number of questions"
            />
            <v-row>
              <v-col cols="12" sm="12">
                <v-select
                  label="Select topics (Maximum of 5)"
                  v-model="currentTournament.topics"
                  :items="allTopics"
                  item-text="name"
                  chips
                  multiple
                  solo
                  return-object
                  v-on:input="limiter"
                  data-cy="Topics"
                ></v-select>
              </v-col>
            </v-row>
          </v-flex>
        </v-layout>
      </v-container>
    </v-card-text>
    <v-card-actions>
      <v-spacer />
      <v-btn color="blue darken-1" @click="cancel" data-cy="cancelButton"
        >Cancel</v-btn
      >
      <v-btn color="blue darken-1" @click="saveTournament" data-cy="saveButton"
        >Save</v-btn
      >
    </v-card-actions>
  </v-card>
</template>

<script lang="ts">
import { Vue, Component } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import Tournament from '@/models/tournament/Tournament';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class CreateTournament extends Vue {
  currentTournament: Tournament = new Tournament();
  allTopics: Topic[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.allTopics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  limiter(topics: Topic[]) {
    if (topics.length > 5) {
      topics.pop();
    }
    return;
  }

  async cancel() {
    await this.$router.push('/student');
  }

  async saveTournament() {
    if (
      this.currentTournament &&
      (!this.currentTournament.name ||
        (!this.currentTournament.numberOfQuestions &&
          !this.currentTournament.beginningTime) ||
        !this.currentTournament.endingTime ||
        !this.currentTournament.topics.length)
    ) {
      await this.$store.dispatch(
        'error',
        'Error:Tournament must have a name, a beginning and a ending time, the number of questions and topics'
      );
      return;
    }
    if (this.currentTournament) {
      debugger;
      try {
        const result = await RemoteServices.createTournament(
          this.currentTournament
        );
        this.$emit('new-tournament', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
    await this.$router.push('enrollTournament');
  }
}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin: auto;
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
