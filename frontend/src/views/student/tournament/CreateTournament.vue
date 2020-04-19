<template >
    <v-card class="container" style="margin-top: 3%;">
      <v-card-title>
        <span class="headline">
          New Tournament on {{$store.getters.getCurrentCourse.name}}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="currentTournament">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-datetime-picker
                  label="Select beginning"
                  v-model="currentTournament.beginningTime">
                  format: "yyyy/MM/dd hh:mm tt"
              </v-datetime-picker>
              <v-datetime-picker
                  label="Select ending"
                  v-model="currentTournament.endingTime">
                  format: "yyyy/MM/dd hh:mm tt"
              </v-datetime-picker>
              <v-text-field
                  v-model="currentTournament.numberOfQuestions"
                  label="Number of questions"
                  data-cy="Number of questions"
              />
              <v-row>
                <v-col cols="12" sm="12">
                  <v-select
                      label="Topics"
                      v-model="currentTournament.topics"
                      :items="allTopics"
                      item-text="name"
                      chips
                      multiple
                      solo
                      return-object
                  ></v-select>
                </v-col>
              </v-row>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions>
    <v-spacer />
  <v-btn
      color="blue darken-1"
      @click="cancel"
      data-cy="cancelButton"
  >Cancel</v-btn
  >
  <v-btn
      color="blue darken-1"
      @click="saveTournament"
      data-cy="saveButton"
  >Save</v-btn
  >
</v-card-actions>
</v-card>
</template>

<script lang="ts">
import {Vue, Component} from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import Tournament from '@/models/tournament/Tournament';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class CreateTournament extends Vue {
  currentTournament: Tournament = new Tournament();

  currentTopicsSearch: string = '';
  currentTopicsSearchText: string = '';
  allTopicsSearch: string = '';
  allTopicsSearchText: string = '';
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

  async cancel() {
    return;
  }

  async saveTournament() {
    if (this.currentTournament && !this.currentTournament.numberOfQuestions &&
        !this.currentTournament.beginningTime && !this.currentTournament.endingTime) {
      await this.$store.dispatch(
          'error',
          'Course must have name, acronym and academicTerm'
      );
      return;
    }
    if (this.currentTournament) {
      debugger;
      try {
        this.currentTournament.beginningTime = "2013-02-12 23:00:00";
        this.currentTournament.endingTime = "2013-02-13 23:00:00";
        const result = await RemoteServices.createTournament(
            this.currentTournament
        );
        this.$emit('new-tournament', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
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
