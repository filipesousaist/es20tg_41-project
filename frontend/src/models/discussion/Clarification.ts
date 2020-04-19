export default class Clarification{
    id: number | null = null;
    text: string = '';
    username: string = '';

    constructor(jsonObj?: Clarification){
        if(jsonObj){
            this.id = jsonObj.id;
            this.text = jsonObj.text;
            this.username = jsonObj.username;
        }
        

    }
    

}