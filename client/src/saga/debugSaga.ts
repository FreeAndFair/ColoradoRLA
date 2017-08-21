import { takeEvery } from 'redux-saga/effects';


export default function* debugSaga() {
    yield takeEvery('*', (a: any) => {
        // tslint:disable
        console.log('[debug]', a.type, a);
        // tslint:enable
    });
}
