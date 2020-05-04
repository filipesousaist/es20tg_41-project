import Question from '@/models/management/Question';
import User from '@/models/user/User';

export default class StudentQuestion {
  id: number | null = null;
  status: string = 'PROPOSED';
  user: User | null = null;
  questionDto: Question = new Question();

  constructor(jsonObj?: StudentQuestion) {
    this.questionDto.status = 'DISABLED';
    if (jsonObj) {
      this.id = jsonObj.id;
      this.user = jsonObj.user;
      this.status = jsonObj.status;
      this.questionDto = new Question(jsonObj.questionDto);
    }
  }
}
