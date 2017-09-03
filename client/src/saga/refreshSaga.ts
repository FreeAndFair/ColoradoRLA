import {
    put,
    takeLatest,
} from 'redux-saga/effects';

import countyDashboardRefresh from 'corla/action/countyDashboardRefresh';
import dosDashboardRefresh from 'corla/action/dos/dashboardRefresh';
import dosFetchAsmState from 'corla/action/dos/fetchAsmState';
import dosFetchContests from 'corla/action/dos/fetchContests';
import fetchAuditBoardAsmState from 'corla/action/fetchAuditBoardAsmState';
import fetchCountyAsmState from 'corla/action/fetchCountyAsmState';


function countyRefresh() {
    countyDashboardRefresh();
    fetchAuditBoardAsmState();
    fetchCountyAsmState();
}

function dosRefresh() {
    dosDashboardRefresh();
    dosFetchAsmState();
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
        'SET_RISK_LIMIT_OK',
        'UPLOAD_RANDOM_SEED_OK',
    ];
    yield takeLatest(dosRefreshActions, dosRefresh);
}
