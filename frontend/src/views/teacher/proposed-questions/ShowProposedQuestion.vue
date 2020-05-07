<template>
  <div>
    <span
      v-html="
        convertMarkDown(
          studentQuestion.questionDto.content,
          studentQuestion.questionDto.image
        )
      "
    />
    <ul>
      <li
        v-for="option in studentQuestion.questionDto.options"
        :key="option.number"
      >
        <span
          v-if="option.correct"
          v-html="convertMarkDown('**[★]** ', null)"
        />
        <span
          v-html="convertMarkDown(option.content, null)"
          v-bind:class="[option.correct ? 'font-weight-bold' : '']"
        />
      </li>
    </ul>
    <br />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import StudentQuestion from '@/models/student_question/StudentQuestion';

@Component
export default class ShowProposedQuestion extends Vue {
  @Prop({ type: StudentQuestion, required: true })
  readonly studentQuestion!: StudentQuestion;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
