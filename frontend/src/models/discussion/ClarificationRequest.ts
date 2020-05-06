import Clarification from '@/models/discussion/Clarification';

export default class ClarificationRequest{
  id!: number;
  title: string = '';
  text: string = '';
  userId: number | null = null;
  username: string = '';
  clarification: Clarification | null = null;
  creationDate: string = '';

  constructor(jsonObj?: ClarificationRequest){
    if(jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.text = jsonObj.text;
      this.userId = jsonObj.userId;
      this.username = jsonObj.username;
      this.clarification = jsonObj.clarification;
      this.creationDate = jsonObj.creationDate;
    }
  }
}