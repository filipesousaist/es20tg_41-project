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
          Clarification Requests
        </span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-requests :question="question" />
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import ShowRequests from '@/views/teacher/requests/ShowRequests.vue';

@Component({
  components: {
    'show-requests': ShowRequests
  }
})
export default class ClarificationRequestsDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) readonly question!: Question;

  created() {}
}
</script>
