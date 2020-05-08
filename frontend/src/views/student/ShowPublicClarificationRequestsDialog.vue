<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-public-clarification-requests-dialog')"
    @keydown.esc="$emit('close-public-clarification-requests-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ question.title }} | Clarification Requests</span>
      </v-card-title>

      <v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-expansion-panels>
              <v-expansion-panel v-for="request in clarificationRequests" :key="request.id">
                <v-expansion-panel-header>
                  <h3>{{ request.title }}</h3> by {{ request.username }}
                </v-expansion-panel-header>
                <v-divider></v-divider>
                <v-expansion-panel-content>
                  <v-flex xs24 sm12 md8>
                    <h3>Request text:</h3>
                  </v-flex>
                  <v-flex xs24 sm12 md8>
                    <p>{{ request.text }}</p>
                  </v-flex>
                  <v-flex xs24 sm12 md8>
                    <h3>Summary by teacher {{ request.clarification.username }}</h3>
                  </v-flex>
                  <v-flex xs24 sm12 md8>{{ request.clarification.summary }}</v-flex>
                </v-expansion-panel-content>
              </v-expansion-panel>
            </v-expansion-panels>

            <v-flex
              v-if="!(!!clarificationRequests) || clarificationRequests.length==0"
              xs24
              sm12
              md8
            >
              <h3>No clarification Requests to show</h3>
            </v-flex>
            <v-card-actions>
              <v-spacer />
              <v-btn
                color="blue darken-1"
                @click="$emit('close-public-clarification-requests-dialog')"
                data-cy="cancelButton"
              >Close</v-btn>
            </v-card-actions>
          </v-layout>
        </v-container>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import ClarificationRequest from '@/models/discussion/ClarificationRequest';
import StatementQuestion from '@/models/statement/StatementQuestion';
import { Store } from 'vuex';

@Component
export default class ShowPublicClarificationRequestDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ }) readonly question!: Question|StatementQuestion;
  @Prop({ type: StatementQuestion }) readonly statementQuestion!: StatementQuestion;


  

  clarificationRequests: ClarificationRequest[] = [];

  async created() {
    console.log('Hello there');
    if(this.question instanceof Question){
      console.log(this.question.id);
      this.clarificationRequests = await RemoteServices.getPublicClarificationRequests(this.question.id);
    }else{
      if(this.question instanceof StatementQuestion){
        console.log(this.question.questionId);
        this.clarificationRequests = await RemoteServices.getPublicClarificationRequests(this.question.questionId);
      }
    }


    console.log(this.clarificationRequests);
    console.log(this.clarificationRequests.length);
    console.log(
      !!!this.clarificationRequests || this.clarificationRequests.length == 0
    );
  }
}
</script>
