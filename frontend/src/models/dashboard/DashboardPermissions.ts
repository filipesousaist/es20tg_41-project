export default class DashboardPermissions {
  showNumProposedQuestions: boolean;
  showNumAcceptedQuestions: boolean;

  // TODO: add properties for each stat permission

  constructor(jsonObj: DashboardPermissions) {
    this.showNumProposedQuestions = jsonObj.showNumProposedQuestions;
    this.showNumAcceptedQuestions = jsonObj.showNumAcceptedQuestions;
  }
}
