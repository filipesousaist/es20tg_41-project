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
                
            </v-flex>
            <v-flex xs24 sm12 md8>

            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions>
         <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-view-clarreq-dialog')"
          data-cy="cancelButton"
          >Close</v-btn
        >



    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ClarificationRequest from '@/models/discussion/ClarificationRequest';
import StatementQuestion from '../../../models/statement/StatementQuestion';
import { Store } from 'vuex';

</script>
