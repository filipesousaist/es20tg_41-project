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
        <span class="headline">Clarification Requests</span>
      </v-card-title>
      <div v-for="request in clarificationRequests" :key="request">
        <v-card-text class="text-left">
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex xs24 sm12 md8>{{ request.title }}</v-flex>
            </v-layout>
          </v-container>
        </v-card-text>
        <v-divider></v-divider>
      </div>
      <div v-if="!(!!clarificationRequests)">
        <v-flex xs24 sm12 md8>No clarification Requests to show</v-flex>
      </div>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-public-clarification-requests-dialog')"
          data-cy="cancelButton"
        >Close</v-btn>
      </v-card-actions>
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
  @Prop({ type: Question, required: true }) readonly question!: Question;

  clarificationRequests!: ClarificationRequest[];

  created() {
    //Call RemoteService to get public clarificationRequests
  }
}
</script>
