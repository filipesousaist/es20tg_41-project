import Topic from '../management/Topic';

export default class Tournament{
  id!: number;
  beginningTime!: string;
  endingTime!: string;
  numberOfQuestions!: number;
  isClosed!:boolean;
  createdByUser!: number;
  name!: string;

  topics: Topic[] = [];

  constructor(jsonObj?: Tournament){
    if(jsonObj) {
      this.id = jsonObj.id;

      this.beginningTime = jsonObj.beginningTime.replace('T', ' ').slice(0,16);
      this.endingTime = jsonObj.endingTime.replace('T', ' ').slice(0,16);
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.isClosed = jsonObj.isClosed;
      this.createdByUser = jsonObj.createdByUser;
      this.name = jsonObj.name;

      if (jsonObj.topics) {
        this.topics = jsonObj.topics.map(
          (topic: Topic) => new Topic(topic)
        );
      }
    }
  }
}