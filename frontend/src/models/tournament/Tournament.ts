import Topic from '../management/Topic';
import StatementQuiz from '@/models/statement/StatementQuiz';
import { ISOtoString } from '@/services/ConvertDateService';

export default class Tournament {
  id!: number;
  beginningTime!: string;
  endingTime!: string;
  numberOfQuestions!: number;
  isClosed!: boolean;
  creatorName!: string;
  name!: string;

  quizDto: StatementQuiz | null = null;
  topics: Topic[] = [];
  studentsUsername: string[] = [];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;

      this.beginningTime = ISOtoString(jsonObj.beginningTime);
      this.endingTime = ISOtoString(jsonObj.endingTime);
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.isClosed = jsonObj.isClosed;
      this.creatorName = jsonObj.creatorName;
      this.name = jsonObj.name;
      if (jsonObj.quizDto) {
        this.quizDto = new StatementQuiz(jsonObj.quizDto);
      }
      if (jsonObj.topics) {
        this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
      }

      if (jsonObj.studentsUsername) {
        let student;
        console.log(jsonObj.studentsUsername)
        while((student = jsonObj.studentsUsername.pop())) {
          this.studentsUsername.push(student);
        }
      }
    }
  }
}
