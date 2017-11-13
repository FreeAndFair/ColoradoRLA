declare namespace Action {
    interface SubmitData<S, R> {
        sent: S;
        received: R;
    }

    type App
        = BallotNotFoundFail
        | BallotNotFoundNetworkFail
        | BallotNotFoundOk
        | BallotNotFoundSend
        | CountyCVRImportFailNotice
        | CountyCVRImportOkNotice
        | CountyDashboardRefreshOk
        | CountyFetchContestsOk
        | CountyFetchCvrOk
        | CountyLoginOk
        | DOSDashboardRefreshOk
        | DOSFetchContestsOk
        | DOSLoginOk
        | FetchAuditBoardASMStateOk
        | FetchCountyASMStateOk
        | FetchCvrsToAuditOk
        | FetchDOSASMStateOk
        | ImportCvrExportOk
        | Login1FOk
        | SelectContestsForAuditOk
        | UpdateAcvrForm
        | UploadBallotManifestOk
        | UploadAcvrFail
        | UploadAcvrNetworkFail
        | UploadAcvrOk
        | UploadAcvrSend
        | UploadCvrExportOk
        | UploadRandomSeedOk
        | UploadingBallotManifest
        | UploadingCvrExport;

    interface BallotNotFoundFail {
        type: 'BALLOT_NOT_FOUND_FAIL',
        data: any,
    }

    interface BallotNotFoundNetworkFail {
        type: 'BALLOT_NOT_FOUND_NETWORK_FAIL',
        data: any,
    }

    interface BallotNotFoundOk {
        type: 'BALLOT_NOT_FOUND_OK',
        data: any,
    }

    interface BallotNotFoundSend {
        type: 'BALLOT_NOT_FOUND_SEND',
        data: any,
    }

    interface CountyCVRImportFailNotice {
        type: 'COUNTY_CVR_IMPORT_FAIL_NOTICE';
        data: any;
    }

    interface CountyCVRImportOkNotice {
        type: 'COUNTY_CVR_IMPORT_OK_NOTICE';
        data: any;
    }

    interface CountyDashboardRefreshOk {
        type: 'COUNTY_DASHBOARD_REFRESH_OK';
        data: any;
    }

    interface CountyFetchContestsOk {
        type: 'COUNTY_FETCH_CONTESTS_OK';
        data: any;
    }

    interface CountyFetchCvrOk {
        type: 'COUNTY_FETCH_CVR_OK';
        data: any;
    }

    interface CountyLoginOk {
        type: 'COUNTY_LOGIN_OK';
        data: any;
    }

    interface DOSDashboardRefreshOk {
        type: 'DOS_DASHBOARD_REFRESH_OK';
        data: any;
    }

    interface DOSFetchContestsOk {
        type: 'DOS_FETCH_CONTESTS_OK';
        data: any;
    }

    interface DOSLoginOk {
        type: 'DOS_LOGIN_OK';
        data: any;
    }

    interface FetchAuditBoardASMStateOk {
        type: 'FETCH_AUDIT_BOARD_ASM_STATE_OK';
        data: any;
    }

    interface FetchCountyASMStateOk {
        type: 'FETCH_COUNTY_ASM_STATE_OK';
        data: JSON.FetchCountyASMStateOk;
    }

    interface FetchCvrsToAuditOk {
        type: 'FETCH_CVRS_TO_AUDIT_OK';
        data: any;
    }

    interface FetchDOSASMStateOk {
        type: 'FETCH_DOS_ASM_STATE_OK';
        data: any;
    }

    interface ImportCvrExportOk {
        type: 'IMPORT_CVR_EXPORT_OK';
        data: any;
    }

    interface Login1FOk {
        type: 'LOGIN_1F_OK';
        data: any;
    }

    interface SelectContestsForAuditOk {
        type: 'SELECT_CONTESTS_FOR_AUDIT_OK';
        data: any;
    }

    interface UpdateAcvrForm {
        type: 'UPDATE_ACVR_FORM';
        data: any;
    }

    interface UploadBallotManifestOk {
        type: 'UPLOAD_BALLOT_MANIFEST_OK';
        data: any;
    }

    interface UploadAcvrFail {
        type: 'UPLOAD_ACVR_FAIL';
        data: any;
    }

    interface UploadAcvrNetworkFail {
        type: 'UPLOAD_ACVR_NETWORK_FAIL';
        data: any;
    }

    interface UploadAcvrOk {
        type: 'UPLOAD_ACVR_OK';
        data: any;
    }

    interface UploadAcvrSend {
        type: 'UPLOAD_ACVR_SEND';
        data: any;
    }

    interface UploadCvrExportOk {
        type: 'UPLOAD_CVR_EXPORT_OK';
        data: any;
    }

    interface UploadRandomSeedOk {
        type: 'UPLOAD_RANDOM_SEED_OK';
        data: any;
    }

    interface UploadingBallotManifest {
        type: 'UPLOADING_BALLOT_MANIFEST';
        data: any;
    }

    interface UploadingCvrExport {
        type: 'UPLOADING_CVR_EXPORT';
        data: any;
    }
}
