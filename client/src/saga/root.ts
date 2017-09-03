import { all } from 'redux-saga/effects';

import * as config from 'corla/config';

import acvrUploadSaga from './acvrUploadSaga';
import auditBoardSignInSaga from './auditBoardSignInSaga';
import ballotNotFoundOkSaga from './ballotNotFoundOkSaga';
import countyDashboardRefreshOkSaga from './countyDashboardRefreshOkSaga';
import countyLoginSaga from './countyLoginSaga';
import countyPollSaga from './countyPollSaga';
import debugSaga from './debugSaga';
import dosAuditSaga from './dosAuditSaga';
import dosLoginSaga from './dosLoginSaga';
import dosPollSaga from './dosPollSaga';
import logoutSaga from './logoutSaga';
import nextLoginSaga from './nextLoginSaga';
import refreshSaga from './refreshSaga';
import sessionSaga from './sessionSaga';
import uploadAcvrOkSaga from './uploadAcvrOkSaga';
import uploadBallotManifestSaga from './uploadBallotManifestSaga';
import uploadCvrExportSaga from './uploadCvrExportSaga';


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
