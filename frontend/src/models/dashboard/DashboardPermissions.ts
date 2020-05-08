export default class DashboardPermissions {
  showNumProposedQuestions: boolean;
  showNumAcceptedQuestions: boolean;
  showTotalTournaments: boolean;
  showHighestResult: boolean;

  // TODO: add properties for each stat permission

  constructor(jsonObj: DashboardPermissions) {
    this.showNumProposedQuestions = jsonObj.showNumProposedQuestions;
    this.showNumAcceptedQuestions = jsonObj.showNumAcceptedQuestions;
    this.showTotalTournaments = jsonObj.showTotalTournaments;
    this.showHighestResult = jsonObj.showHighestResult;
  }
}
