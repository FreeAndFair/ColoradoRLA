import store from '../store';


export default (type: string, data: any = {}) =>
    store.dispatch({ data, type });
