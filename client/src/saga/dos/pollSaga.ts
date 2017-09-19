import { delay } from 'redux-saga';
import {
    call,
    select,
    takeLatest,
} from 'redux-saga/effects';

import dashboardRefresh from 'corla/action/dos/dashboardRefresh';
import fetchAsmState from 'corla/action/dos/fetchAsmState';
import fetchContests from 'corla/action/dos/fetchContests';


const DOS_POLL_DELAY = 1000 * 5;

function* pollTask() {
    while (true) {
        const { dashboard, loggedIn } = yield select();

        if (!loggedIn) { return null; }
        if (dashboard !== 'sos') { return null; }

        dashboardRefresh();
        fetchAsmState();
        fetchContests();

        yield delay(DOS_POLL_DELAY);
    }
}


export default function* pollSaga() {
    yield takeLatest('DOS_POLL', pollTask);
}
