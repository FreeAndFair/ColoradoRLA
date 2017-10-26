import { has } from 'lodash';

import {
    put,
    select,
    takeLatest,
} from 'redux-saga/effects';

import notice from 'corla/notice';

import countyFetchContests from 'corla/action/county/fetchContests';
import fetchCvrsToAudit from 'corla/action/county/fetchCvrsToAudit';

import { parse } from 'corla/adapter/countyDashboardRefresh';

import currentBallotIdSelector from 'corla/selector/county/currentBallotId';


function* countyRefreshOk({ data }: any): any {
    const state = yield select();

    const pending = state.cvrImportPending;
    const status = state.cvrImportStatus;

    if (pending.started < status.timestamp && !pending.alerted) {
        switch (status.state) {
            case 'FAILED': {
                yield put({
                    data: { status },
                    type: 'COUNTY_CVR_IMPORT_FAIL_NOTICE',
                });
                break;
            }
            case 'SUCCESSFUL': {
                yield put({ type: 'COUNTY_CVR_IMPORT_OK_NOTICE' });
                break;
            }
        }
    }

    const county = parse(data, state);

    if (county.id) {
        countyFetchContests(county.id);
    }

    if (has(county, 'currentRound.number')) {
        fetchCvrsToAudit(county.currentRound!.number);
    }
}

function* cvrImportFail(action: any): IterableIterator<any> {
    const { data } = action;
    const { status } = data;
    const { error } = status;

    if (error) {
        notice.danger(`Failed to import CVRs: ${error}`);
    } else {
        notice.danger('Failed to import CVR export file.');
    }

}

function* cvrImportOk(): IterableIterator<any> {
    notice.ok(`Imported CVR export.`);
}

export default function* dosLoginSaga() {
    yield takeLatest('COUNTY_DASHBOARD_REFRESH_OK', countyRefreshOk);

    yield takeLatest('COUNTY_CVR_IMPORT_FAIL_NOTICE', cvrImportFail);
    yield takeLatest('COUNTY_CVR_IMPORT_OK_NOTICE', cvrImportOk);
}
