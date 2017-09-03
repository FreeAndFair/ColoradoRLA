import store from 'corla/store';


export default (type: string, data: any = {}) =>
    store.dispatch({ data, type });
