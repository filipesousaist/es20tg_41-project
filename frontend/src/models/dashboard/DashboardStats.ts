export default class DashboardStats {
  userId: number;
  username: string;
  name: string;

  /*Tournament properties*/
  totalTournaments!: number;
  highestResult!: number;
  numProposedQuestions: number;
  numAcceptedQuestions: number;

  // TODO: add properties for each stat

  constructor(jsonObj: DashboardStats) {
    this.userId = jsonObj.userId;
    this.username = jsonObj.username;
    this.name = jsonObj.name;
    this.totalTournaments = jsonObj.totalTournaments;
    this.highestResult = jsonObj.highestResult;
    this.numProposedQuestions = jsonObj.numProposedQuestions;
    this.numAcceptedQuestions = jsonObj.numAcceptedQuestions;
  }
}
