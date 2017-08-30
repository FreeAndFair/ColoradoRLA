import * as _ from 'lodash';

import countyDashboardRefreshOk from './countyDashboardRefreshOk';
import countyFetchAllCvrsOk from './countyFetchAllCvrsOk';
import countyFetchContestsOk from './countyFetchContestsOk';
import countyFetchCvrOk from './countyFetchCvrOk';
import countyLoginOk from './countyLoginOk';
import dosContestFetchOk from './dosContestFetchOk';
import dosDashboardRefreshOk from './dosDashboardRefreshOk';
import dosLoginOk from './dosLoginOk';
import fetchAuditBoardAsmStateOk from './fetchAuditBoardAsmStateOk';
import fetchCountyAsmStateOk from './fetchCountyAsmStateOk';
import fetchCvrsToAuditOk from './fetchCvrsToAuditOk';
import fetchDosAsmStateOk from './fetchDosAsmStateOk';
import selectContestsForAuditOk from './selectContestsForAuditOk';
import setRiskLimitOk from './setRiskLimitOk';
import updateAcvrForm from './updateAcvrForm';
import uploadAcvrOk from './uploadAcvrOk';
import uploadBallotManifestOk from './uploadBallotManifestOk';
import uploadCvrExportOk from './uploadCvrExportOk';
import uploadRandomSeedOk from './uploadRandomSeedOk';


interface AppState {
    loggedIn: boolean;
    dashboard?: Dashboard;
    county?: any;
    sos?: any;
}

const defaultState = {
    loggedIn: false,
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

    case 'SELECT_CONTESTS_FOR_AUDIT_OK': {
        return selectContestsForAuditOk(state, action);
    }

    case 'SET_RISK_LIMIT_OK': {
        return setRiskLimitOk(state, action);
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
