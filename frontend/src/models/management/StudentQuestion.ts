import Question from '@/models/management/Question';
import User from '@/models/user/User';

export default class StudentQuestion {
  id: number | null = null;
  user: User | null = null;
  questionDto: Question = new Question();
  ser: number | null = null;

  constructor(jsonObj?: StudentQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.user = jsonObj.user;
      this.questionDto = new Question(jsonObj.questionDto);
    }
  }
}
