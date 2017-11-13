import { all } from 'redux-saga/effects';

import * as config from 'corla/config';

import acvrUploadSaga from './county/acvrUploadSaga';
import auditBoardSignInSaga from './county/auditBoardSignInSaga';
import ballotNotFoundOkSaga from './county/ballotNotFoundOkSaga';
import countyDashboardRefreshOkSaga from './county/dashboardRefreshOkSaga';
import countySyncSaga from './county/sync';
import uploadAcvrOkSaga from './county/uploadAcvrOkSaga';
import uploadBallotManifestSaga from './county/uploadBallotManifestSaga';
import uploadCvrExportSaga from './county/uploadCvrExportSaga';

import debugSaga from './debugSaga';

import dosAuditSaga from './dos/auditSaga';
import dosSyncSaga from './dos/sync';

import loginSaga from './loginSaga';
import logoutSaga from './logoutSaga';
import syncSaga from './sync';


export default function* rootSaga() {
    const sagas = [
        acvrUploadSaga(),
        auditBoardSignInSaga(),
        ballotNotFoundOkSaga(),
        countyDashboardRefreshOkSaga(),
        countySyncSaga(),
        dosAuditSaga(),
        dosSyncSaga(),
        loginSaga(),
        logoutSaga(),
        syncSaga(),
        uploadAcvrOkSaga(),
        uploadBallotManifestSaga(),
        uploadCvrExportSaga(),
    ];

    if (config.debug) {
        sagas.push(debugSaga());
    }

    yield all(sagas);
}
