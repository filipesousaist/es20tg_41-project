import Topic from '../management/Topic';

export default class Tournament{
  id!: number;
  beginningTime!: string | undefined;
  endingTime!: string | undefined;
  numberOfQuestions!: number;

  topics: Topic[] = [];

  constructor(jsonObj?: Tournament){
    if(jsonObj) {
      this.id = jsonObj.id;
      this.beginningTime = jsonObj.beginningTime;
      this.endingTime = jsonObj.endingTime;
      this.numberOfQuestions = jsonObj.numberOfQuestions;

      if (jsonObj.topics) {
        this.topics = jsonObj.topics.map(
          (topic: Topic) => new Topic(topic)
        );
      }
    }
  }
}