<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{ question.title }}
        </span>
      </v-card-title>
      <v-card>
        <span v-for="request in this.question.requests" :key="request.title">
          <v-card-text class="text-left">
            <span class="title">
              {{ request.title }}
            </span>
            <br />
            <li>
              {{ request.text }}
            </li>
            <div v-if="request.clarification != null">
              <v-flex xs24 sm12 md8>
                <b>{{ request.clarification.username }} said:</b>
              </v-flex>
              <v-flex xs24 sm12 md8>
                <p>
                  {{ request.clarification.text }}
                </p>
              </v-flex>
            </div>
            <div v-else>
              <v-text-field
                v-model="currentClarification.text"
                label="Text"
                data-cy="clarificationText"
              />
              <v-btn
                color="blue"
                @click="saveClarification(request)"
                data-cy="submitClarification"
                >Submit</v-btn
              >
            </div>
            <p></p>
            <v-divider></v-divider>
          </v-card-text>
        </span>

        <v-card-actions>
          <v-spacer />
          <v-btn
            color="blue darken-1"
            data-cy="closeButton"
            @click="$emit('dialog', false)"
            >Close</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import Clarification from '@/models/discussion/Clarification';
import ClarificationRequest from '@/models/discussion/ClarificationRequest';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ClarificationRequestsDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) readonly question!: Question;

  currentClarification!: Clarification | null;

  created() {
    this.currentClarification = new Clarification();
  }

  async saveClarification(request: ClarificationRequest) {
    if (this.currentClarification && !this.currentClarification.text) {
      await this.$store.dispatch(
        'error',
        'Error: Clarification Request must have text.'
      );
      this.currentClarification = null;
      return;
    }
    if (this.currentClarification) {
      this.currentClarification.username = this.$store.getters.getUser.username;
      try {
        const result = await RemoteServices.createClarification(this.currentClarification, request.id);
        this.$emit('new-clarification-request', result);
        request.clarification = result;
        this.currentClarification.text = '';
      } catch (error) {
        await this.$store.dispatch('error', 'Error' + error);
      }
    }
  }
}
</script>
