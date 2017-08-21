import store from '../store';


export default (type: any, data: any = {}) =>
    store.dispatch({ data, type });
