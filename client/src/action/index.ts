import { store } from '..';


export default (type: any, data: any) => store.dispatch({ data, type});
