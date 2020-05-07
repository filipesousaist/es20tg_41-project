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
          New Clarification Request
        </span>
      </v-card-title>

      <v-card-text class="text-left">

         <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="clarificationRequest.title"
                label="Title"
                data-cy="clarificationRequestTitle"
              />
            </v-flex>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="clarificationRequest.text"
                label="Text"
                data-cy="clarificationRequestText"
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
        <v-btn color="blue" @click="saveClarificationRequest" data-cy="submitClarificationRequest"
          >Submit</v-btn>
      </v-card-actions>


    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ClarificationRequest from '@/models/discussion/ClarificationRequest';
import StatementQuestion from '@/models/statement/StatementQuestion';
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
    let result;
    console.log(this.$store.getters.getUser);
    this.clarificationRequest.userId = this.$store.getters.getUser.id;
    if (this.clarificationRequest
    && !this.clarificationRequest.title 
    || !this.clarificationRequest.text){

        await this.$store.dispatch('error', 'Error:Clarification Request must have a title and a text.');
        return;
    }

    if(this.clarificationRequest){
      try{
        result = await RemoteServices.createClarificationRequest(this.clarificationRequest, this.question.questionId);
        this.$emit('new-clarification-request', result);
      }catch(error){
        await this.$store.dispatch('error', error);
      }
    }
    if(result) this.question.clarificationRequest = result;
    this.$emit('close-dialog');
  }
}
</script>
