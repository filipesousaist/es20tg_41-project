export default class Comment{
  id!: number;
  text: string = '';
  userId: number | null = null;
  username: string = '';
  creationDate: string = '';

  constructor(jsonObj?: Comment){
    if(jsonObj) {
      this.id = jsonObj.id;
      this.text = jsonObj.text;
      this.userId = jsonObj.userId;
      this.username = jsonObj.username;
      this.creationDate = jsonObj.creationDate;
    }
  }
}