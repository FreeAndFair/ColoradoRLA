import {
    put,
    takeLatest,
} from 'redux-saga/effects';

import countyDashboardRefresh from 'corla/action/county/dashboardRefresh';
import fetchAuditBoardAsmState from 'corla/action/county/fetchAuditBoardAsmState';
import fetchCountyAsmState from 'corla/action/county/fetchCountyAsmState';

import dosDashboardRefresh from 'corla/action/dos/dashboardRefresh';
import dosFetchContests from 'corla/action/dos/fetchContests';


function countyRefresh() {
    countyDashboardRefresh();
    fetchAuditBoardAsmState();
    fetchCountyAsmState();
}

function dosRefresh() {
    dosDashboardRefresh();
    dosFetchContests();
}


export default function* dosLoginSaga() {
    const countyRefreshActions = [
        'AUDIT_BOARD_SIGN_IN_OK',
        'AUDIT_BOARD_SIGN_OUT_OK',
        'ESTABLISH_AUDIT_BOARD_OK',
        'SUBMIT_ROUND_SIGN_OFF_OK',
        'UPLOAD_ACVR_OK',
        'UPDATE_ACVR_FORM',
        'UPLOAD_BALLOT_MANIFEST_OK',
        'UPLOAD_CVR_EXPORT_OK',
        'IMPORT_BALLOT_MANIFEST_OK',
        'IMPORT_CVR_EXPORT_OK',
    ];
    yield takeLatest(countyRefreshActions, countyRefresh);

    const dosRefreshActions = [
        'DOS_START_NEXT_ROUND_OK',
        'PUBLISH_BALLOTS_TO_AUDIT_OK',
        'SELECT_CONTESTS_FOR_AUDIT_OK',
        'SET_AUDIT_INFO_OK',
        'UPLOAD_RANDOM_SEED_OK',
    ];
    yield takeLatest(dosRefreshActions, dosRefresh);
}
