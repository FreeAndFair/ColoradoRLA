import { all } from 'redux-saga/effects';

import * as config from '../config';

import ballotNotFoundOkSaga from './ballotNotFoundOkSaga';
import countyDashboardRefreshOkSaga from './countyDashboardRefreshOkSaga';
import countyLoginSaga from './countyLoginSaga';
import countyPollSaga from './countyPollSaga';
import debugSaga from './debugSaga';
import dosAuditSaga from './dosAuditSaga';
import dosLoginSaga from './dosLoginSaga';
import dosPollSaga from './dosPollSaga';
import fileUploadSaga from './fileUploadSaga';
import logoutSaga from './logoutSaga';
import uploadBallotManifestSaga from './next/uploadBallotManifestSaga';
import uploadCvrExportSaga from './next/uploadCvrExportSaga';
import refreshSaga from './refreshSaga';
import uploadAcvrOkSaga from './uploadAcvrOkSaga';


export default function* rootSaga() {
    const sagas = [
        ballotNotFoundOkSaga(),
        countyLoginSaga(),
        countyPollSaga(),
        countyDashboardRefreshOkSaga(),
        dosAuditSaga(),
        dosLoginSaga(),
        dosPollSaga(),
        fileUploadSaga(),
        logoutSaga(),
        refreshSaga(),
        uploadAcvrOkSaga(),
        uploadBallotManifestSaga(),
        uploadCvrExportSaga(),
    ];

    if (config.debug) {
        sagas.push(debugSaga());
    }

    yield all(sagas);
}
