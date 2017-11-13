import {
    put,
    takeLatest,
} from 'redux-saga/effects';

import countyDashboardRefresh from 'corla/action/county/dashboardRefresh';
import fetchAuditBoardASMState from 'corla/action/county/fetchAuditBoardASMState';
import fetchCountyASMState from 'corla/action/county/fetchCountyASMState';

import dosDashboardRefresh from 'corla/action/dos/dashboardRefresh';
import dosFetchContests from 'corla/action/dos/fetchContests';


function countyRefresh() {
    countyDashboardRefresh();
    fetchAuditBoardASMState();
    fetchCountyASMState();
}

function dosRefresh() {
    dosDashboardRefresh();
    dosFetchContests();
}


export default function* dosLoginSaga() {
    const countyRefreshActions = [
        'AUDIT_BOARD_SIGN_IN_OK',
        'AUDIT_BOARD_SIGN_OUT_OK',
        'BALLOT_NOT_FOUND_FAIL',
        'BALLOT_NOT_FOUND_NETWORK_FAIL',
        'BALLOT_NOT_FOUND_OK',
        'SUBMIT_ROUND_SIGN_OFF_OK',
        'UPLOAD_ACVR_FAIL',
        'UPLOAD_ACVR_NETWORK_FAIL',
        'UPLOAD_ACVR_OK',
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
        'SET_HAND_COUNT_OK',
        'UPLOAD_RANDOM_SEED_OK',
    ];
    yield takeLatest(dosRefreshActions, dosRefresh);
}
