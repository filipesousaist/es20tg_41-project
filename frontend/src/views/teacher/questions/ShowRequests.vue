<template>
  <div>
    <span class="headline">
      {{ request.title }}
    </span>
    <span>
      {{ request.text }}
    </span>
  </div>
</template>








<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import ClarificationRequest from '@/models/discussion/ClarificationRequest';

@Component
export default class ShowRequests extends Vue {
  @Prop({ type: ClarificationRequest, required: true }) readonly request!: ClarificationRequest;
}


</script>
