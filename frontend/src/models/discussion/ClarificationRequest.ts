import Clarification from '@/models/discussion/Clarification';

export default class ClarificationRequest{
  id!: number;
  title: string = '';
  text: string = '';
  username: string = '';
  clarification: Clarification | null = null;

  constructor(jsonObj?: ClarificationRequest){
    if(jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.text = jsonObj.text;
      this.username = jsonObj.username;
      this.clarification = jsonObj.clarification;
    }
  }
}