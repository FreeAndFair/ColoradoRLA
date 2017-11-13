import store from 'corla/store';


export default function action<D>(type: string, data: (D | {}) = {}) {
    store.dispatch({ data, type });
}
