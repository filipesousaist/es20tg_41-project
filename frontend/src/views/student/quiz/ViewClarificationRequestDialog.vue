<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-view-clarreq-dialog')"
    @keydown.esc="$emit('close-view-clarreq-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          Discussion
        </span>
      </v-card-title>

      <v-card-text class="text-left">

         <v-container grid-list-md fluid>
           
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
                <h2>Clarification Request</h2>
            </v-flex>
            <v-flex xs24 sm12 md8>
                <b>You asked at {{ clarificationRequest.creationDate }}:</b>
            </v-flex>
            <v-flex xs24 sm12 md8>
                <b>{{ clarificationRequest.title }}</b>
            </v-flex>
            <v-flex xs24 sm12 md8>
              <p>
                {{ clarificationRequest.text }}
              </p>
            </v-flex>

            <div v-if="!!clarification">
              <v-divider></v-divider>
              <v-flex xs24 sm12 md8>
                <h2>Clarification</h2>
              </v-flex>
              <v-flex xs24 sm12 md8>
                <b>{{ clarification.username }} said at {{ clarification.creationDate }}:</b>
              </v-flex>
              <v-flex xs24 sm12 md8>
                <p>
                  {{ clarificationRequest.clarification.text }}
                </p>
              </v-flex>

              <v-divider></v-divider>

              <div v-if="clarification.summary != null">
                <v-flex x24 sm12 md8>
                  <h2>DiscussionSummary</h2>
                </v-flex>
                <v-flex xs24 sm12 md8>
                  {{ clarificationRequest.clarification.summary }}
                </v-flex>
              </div>

              <v-divider></v-divider>

              <comment-view
                v-model="CommentView"
                :clarification="clarification"
              />
            </div>
          </v-layout>
          </v-container>
      </v-card-text>
      <v-card-actions>
         <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-view-clarreq-dialog')"
          data-cy="closeButton"
          >Close</v-btn>
      </v-card-actions>
    </v-card>
  
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ClarificationRequest from '@/models/discussion/ClarificationRequest';
import Clarification from '@/models/discussion/Clarification';
import Comment from '@/models/discussion/Comment';
import CommentView from '@views/CommentView.vue';


import StatementQuestion from '../../../models/statement/StatementQuestion';
import { Store } from 'vuex';

@Component({
  components: {
    'comment-view': CommentView,
  }
})
export default class ClarificationRequestDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ClarificationRequest, required: true }) readonly clarificationRequest! : ClarificationRequest;
  @Prop({ type: Clarification, required: true }) clarification! : Clarification|null;


}
</script>

<style lang="scss" scoped>

.clarification-color {
  color: #000;
}
</style>
