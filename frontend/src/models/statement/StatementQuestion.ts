import StatementOption from '@/models/statement/StatementOption';
import Image from '@/models/management/Image';
import { _ } from 'vue-underscore';
import ClarificationRequest from '../discussion/ClarificationRequest';

export default class StatementQuestion {
  questionId!: number;
  quizQuestionId!: number;
  content!: string;
  image: Image | null = null;

  options: StatementOption[] = [];

  constructor(jsonObj?: StatementQuestion) {
    if (jsonObj) {
      console.log(jsonObj);
      this.questionId = jsonObj.questionId;
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.content = jsonObj.content;
      this.image = jsonObj.image;

      if (jsonObj.options) {
        this.options = _.shuffle(
          jsonObj.options.map(
            (option: StatementOption) => new StatementOption(option)
          )
        );
      }
    }
  }
}
