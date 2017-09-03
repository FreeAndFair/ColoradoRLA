import { all } from 'redux-saga/effects';

import * as config from 'corla/config';

import acvrUploadSaga from './county/acvrUploadSaga';
import auditBoardSignInSaga from './county/auditBoardSignInSaga';
import ballotNotFoundOkSaga from './county/ballotNotFoundOkSaga';
import countyDashboardRefreshOkSaga from './county/dashboardRefreshOkSaga';
import countyLoginSaga from './county/loginSaga';
import countyPollSaga from './county/pollSaga';
import uploadAcvrOkSaga from './county/uploadAcvrOkSaga';
import uploadBallotManifestSaga from './county/uploadBallotManifestSaga';
import uploadCvrExportSaga from './county/uploadCvrExportSaga';

import debugSaga from './debugSaga';

import dosAuditSaga from './dos/auditSaga';
import dosLoginSaga from './dos/loginSaga';
import dosPollSaga from './dos/pollSaga';

import logoutSaga from './logoutSaga';
import nextLoginSaga from './nextLoginSaga';
import refreshSaga from './refreshSaga';
import sessionSaga from './sessionSaga';


export default function* rootSaga() {
    const sagas = [
        acvrUploadSaga(),
        auditBoardSignInSaga(),
        ballotNotFoundOkSaga(),
        countyLoginSaga(),
        countyPollSaga(),
        countyDashboardRefreshOkSaga(),
        dosAuditSaga(),
        dosLoginSaga(),
        dosPollSaga(),
        logoutSaga(),
        nextLoginSaga(),
        refreshSaga(),
        sessionSaga(),
        uploadAcvrOkSaga(),
        uploadBallotManifestSaga(),
        uploadCvrExportSaga(),
    ];

    if (config.debug) {
        sagas.push(debugSaga());
    }

    yield all(sagas);
}
