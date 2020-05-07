import Comment from '@/models/discussion/Comment';

export default class Clarification {
  id!: number;
  text: string = '';
  userId: number | null = null;
  username: string = '';
  creationDate: string = '';
  summary: string | null = null;
  commentList: Comment[] = []

  constructor(jsonObj?: Clarification) {
    if (jsonObj) {
      console.log(jsonObj);
      this.id = jsonObj.id;
      this.username = jsonObj.username;
      this.text = jsonObj.text;
      this.userId = jsonObj.userId;
      this.creationDate = jsonObj.creationDate;
      this.summary = jsonObj.summary;

      this.commentList = jsonObj.commentList.map(
        (comment: Comment) => new Comment(comment)
      );

    }
  }
}

