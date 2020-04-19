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
          New Clarification Request for question {{ question.questionId }}
        </span>
      </v-card-title>

      <v-card-text class="text-left">

         <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="clarificationRequest.title"
                label="Title"
              />
            </v-flex>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="clarificationRequest.text"
                label="Text"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions>
         <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn color="blue" @click="saveClarificationRequest"
          >Submit</v-btn>
      </v-card-actions>


    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ClarificationRequest from '@/models/discussion/ClarificationRequest';
import StatementQuestion from '../../../models/statement/StatementQuestion';
import { Store } from 'vuex';

@Component
export default class ClarificationRequestDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StatementQuestion, required: true }) readonly question! : StatementQuestion;

  clarificationRequest!: ClarificationRequest;

  created() {
    this.clarificationRequest = new ClarificationRequest();
  }

  async saveClarificationRequest(){
    this.clarificationRequest.username = this.$store.getters.getUser.username;
    if (this.clarificationRequest
    && !this.clarificationRequest.title 
    || !this.clarificationRequest.text){

        await this.$store.dispatch('error', 'Clarification Request must have a title and a text.');
        return;
    }

    if(this.clarificationRequest){
      try{
        const result = await RemoteServices.createClarificationRequest(this.clarificationRequest, this.question.questionId);
        this.$emit('new-clarification-request', result);
      }catch(error){
        await this.$store.dispatch('error', error);
      }
    }
    this.$emit('close-dialog');
  }
}
</script>
