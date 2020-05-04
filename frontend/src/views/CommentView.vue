<template>
<v-card>
      <v-card-text class="text-left">
              <v-flex xs24 sm12 md8>
                <h2>Followup Comments</h2>
              </v-flex>

            <div v-for="comment in clarification.commentList" :key="comment.id">
                 <v-flex xs24 sm12 md8>
                  <b>{{ comment.username}}:</b>{{ comment.text }}
                </v-flex>
            </div>

            <v-flex xs24 sm12 md8>
            <v-text-field
                label="Text"
                v-model="comment.text"
              />
            </v-flex>
            <v-card-actions>
         <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="createComment"
          >Send</v-btn
        >
      </v-card-actions>
      </v-card-text>
</v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import Clarification from '@/models/discussion/Clarification';
import ClarificationRequest from '@/models/discussion/ClarificationRequest';
import RemoteServices from '@/services/RemoteServices';
import Comment from '@/models/discussion/Comment';

@Component
export default class CommentView extends Vue{
    @Prop({ type: Clarification, required: true }) clarification! : Clarification|null;


    comment! : Comment;

  async created(){
    this.comment = new Comment();
  }

  async createComment(){
    let result;
    this.comment.userId = this.$store.getters.getUser.id;
    if (this.comment
    && !this.comment.text){

        await this.$store.dispatch('error', 'Error:Comment must have a text.');
        return;
    }

    if(!!this.comment && !!this.clarification){
      try{
        result = await RemoteServices.createComment(this.comment, this.clarification.id);
        this.$emit('new-clarification-request', result);
        this.comment.username = this.$store.getters.getUser.username;
        this.clarification.commentList.push(this.comment);
        console.log(this.clarification);
        this.comment = new Comment();
        this.comment.text = '';
      }catch(error){
        await this.$store.dispatch('error', error);
      }

      
    }
  }

}

</script>
