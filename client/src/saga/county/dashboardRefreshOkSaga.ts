import { has } from 'lodash';

import {
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

    switch (state.cvrImportAlert) {
    case 'None': break;
    case 'Fail':
        notice.danger('Failed to import CVR export.');
        break;
    case 'Ok':
        notice.ok(`Imported CVR export.`);
        break;
    }

    const county = parse(data, state);

    if (county.id) {
        countyFetchContests(county.id);
    }

    if (has(county, 'currentRound.number')) {
        fetchCvrsToAudit(county.currentRound!.number);
    }
}

export default function* dosLoginSaga() {
    yield takeLatest('COUNTY_DASHBOARD_REFRESH_OK', countyRefreshOk);
}
