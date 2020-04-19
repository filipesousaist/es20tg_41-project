<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
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
            <v-text-field
              v-model="clarification.text"
              label="Text"
            />
            <v-btn color="blue" @click="saveClarification(request)"
            >Submit</v-btn>
          </v-card-text>
        </span>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" @click="closeRequestsDialog"
            >close</v-btn
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

  clarification!: Clarification | null;

  created() {
    this.clarification = new Clarification();
  }

  async saveClarification(request: ClarificationRequest) {
    if (this.clarification && !this.clarification.text) {
      await this.$store.dispatch(
        'error',
        'Clarification Request must have a title and a text.'
      );
      this.clarification = null;
      return;
    }
    if (this.clarification) {
      this.clarification.username = this.$store.getters.getUser.username;
      try {
        const result = await RemoteServices.createClarification(this.clarification, request.id);
        this.$emit('new-clarification-request', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  closeRequestsDialog() {
    this.$emit('close-show-clarification-requests-dialog');
  }
}
</script>
