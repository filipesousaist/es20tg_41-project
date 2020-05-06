export default class DashboardStats {
  userId: number;
  username: string;
  name: string;

  numProposedQuestions: number;
  numAcceptedQuestions: number;

  // TODO: add properties for each stat

  constructor(jsonObj: DashboardStats) {
    this.userId = jsonObj.userId;
    this.username = jsonObj.username;
    this.name = jsonObj.name;
    this.numProposedQuestions = jsonObj.numProposedQuestions;
    this.numAcceptedQuestions = jsonObj.numAcceptedQuestions;
  }
}
