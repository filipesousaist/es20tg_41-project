export default class DashboardStats {
  userId: number;
  username: string;
  name: string;

  // TODO: add properties for each stat

  constructor(jsonObj: DashboardStats) {
    this.userId = jsonObj.userId;
    this.username = jsonObj.username;
    this.name = jsonObj.name;
  }
}
