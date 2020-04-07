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
              <v-text-field
                label="Title"
                data-cy="Name"
              />

            <v-flex xs24 sm12 md8>
              <v-text-field
                label="Text"
                data-cy="Acronym"
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
        <v-btn color="blue darken-1"
          >Submit</v-btn>
      </v-card-actions>


    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ClarificationRequestDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;

  created() {

  }
}
</script>
