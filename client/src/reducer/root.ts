import * as _ from 'lodash';

import countyDashboardRefreshOk from './county/dashboardRefreshOk';
import countyFetchAllCvrsOk from './county/fetchAllCvrsOk';
import fetchAuditBoardAsmStateOk from './county/fetchAuditBoardAsmStateOk';
import countyFetchContestsOk from './county/fetchContestsOk';
import fetchCountyAsmStateOk from './county/fetchCountyAsmStateOk';
import countyFetchCvrOk from './county/fetchCvrOk';
import fetchCvrsToAuditOk from './county/fetchCvrsToAuditOk';
import countyLoginOk from './county/loginOk';
import updateAcvrForm from './county/updateAcvrForm';
import uploadAcvrOk from './county/uploadAcvrOk';
import uploadBallotManifestOk from './county/uploadBallotManifestOk';
import uploadCvrExportOk from './county/uploadCvrExportOk';

import dosContestFetchOk from './dos/contestFetchOk';
import dosDashboardRefreshOk from './dos/dashboardRefreshOk';
import fetchDosAsmStateOk from './dos/fetchDosAsmStateOk';
import dosLoginOk from './dos/loginOk';
import selectContestsForAuditOk from './dos/selectContestsForAuditOk';
import uploadRandomSeedOk from './dos/uploadRandomSeedOk';

import login1FOk from './login1FOk';


interface AppState {
    loggedIn: boolean;
    loginChallenge: any;
    dashboard?: Dashboard;
    county?: any;
    sos?: any;
}

const defaultState: AppState = {
    loggedIn: false,
    loginChallenge: null,
};


export default function root(state: AppState = defaultState, action: any) {
    switch (action.type) {

    case 'COUNTY_DASHBOARD_REFRESH_OK': {
        return countyDashboardRefreshOk(state, action);
    }

    case 'COUNTY_FETCH_ALL_CVRS_OK': {
        return countyFetchAllCvrsOk(state, action);
    }

    case 'COUNTY_FETCH_CONTESTS_OK': {
        return countyFetchContestsOk(state, action);
    }

    case 'COUNTY_FETCH_CVR_OK': {
        return countyFetchCvrOk(state, action);
    }

    case 'COUNTY_LOGIN_OK': {
        return countyLoginOk(state);
    }

    case 'DOS_DASHBOARD_REFRESH_OK': {
        return dosDashboardRefreshOk(state, action);
    }

    case 'DOS_FETCH_CONTESTS_OK': {
        return dosContestFetchOk(state, action);
    }

    case 'DOS_LOGIN_OK': {
        return dosLoginOk(state);
    }

    case 'FETCH_AUDIT_BOARD_ASM_STATE_OK': {
        return fetchAuditBoardAsmStateOk(state, action);
    }

    case 'FETCH_COUNTY_ASM_STATE_OK': {
        return fetchCountyAsmStateOk(state, action);
    }

    case 'FETCH_CVRS_TO_AUDIT_OK': {
        return fetchCvrsToAuditOk(state, action);
    }

    case 'FETCH_DOS_ASM_STATE_OK': {
        return fetchDosAsmStateOk(state, action);
    }

    case 'LOGIN_1F_OK': {
        return login1FOk(state, action);
    }

    case 'SELECT_CONTESTS_FOR_AUDIT_OK': {
        return selectContestsForAuditOk(state, action);
    }

    case 'UPDATE_ACVR_FORM': {
        return updateAcvrForm(state, action);
    }

    case 'UPLOAD_BALLOT_MANIFEST_OK': {
        return uploadBallotManifestOk(state, action);
    }

    case 'UPLOAD_ACVR_OK': {
        return uploadAcvrOk(state, action);
    }

    case 'UPLOAD_CVR_EXPORT_OK': {
        return uploadCvrExportOk(state, action);
    }

    case 'UPLOAD_RANDOM_SEED_OK': {
        return uploadRandomSeedOk(state, action);
    }

    default:
        return state;
    }
}
