export default class ClarificationRequest{
  id: number | null = null;
  title: string = '';
  text: string = '';
  username: string = '';

  constructor(jsonObj?: ClarificationRequest){
    if(jsonObj){
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.text = jsonObj.text;
      this.username = jsonObj.username;
    }
  }
}