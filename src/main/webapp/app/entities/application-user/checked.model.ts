export interface IChecked {
  id?: string;
  name?: string;
  checked?: boolean;
}

export class Checked implements IChecked {
  constructor(public id?: string, public name?: string, public checked?: boolean) {}
}
