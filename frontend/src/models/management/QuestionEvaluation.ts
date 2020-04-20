export default class QuestionEvaluation {
  id: number | null = null;
  approved: boolean | null = null;
  justification: String | null = null;

  constructor(jsonObj?: QuestionEvaluation) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.approved = jsonObj.approved;
      this.justification = jsonObj.justification;
    }
  }
}
