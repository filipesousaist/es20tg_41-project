export default class DashboardPermissions {
  showNumProposedQuestions: boolean;
  showNumAcceptedQuestions: boolean;
  showNumClarificationRequests: boolean;
  showNumAnsweredClarificationRequests: boolean;

  // TODO: add properties for each stat permission

  constructor(jsonObj: DashboardPermissions) {
    this.showNumProposedQuestions = jsonObj.showNumProposedQuestions;
    this.showNumAcceptedQuestions = jsonObj.showNumAcceptedQuestions;
    this.showNumClarificationRequests = jsonObj.showNumClarificationRequests;
    this.showNumAnsweredClarificationRequests = jsonObj.showNumAnsweredClarificationRequests;


  }
}
