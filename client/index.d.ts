type Dashboard = 'sos' | 'county';

interface Election {
    date: Date;
    type: ElectionType;
}

type ElectionType = 'coordinated'
                  | 'primary'
                  | 'general'
                  | 'recall';

type LoginChallengeBox = [string, string];

type LoginChallenge = LoginChallengeBox[];

interface AppState {
    loginChallenge?: Option<LoginChallenge>;
    dashboard?: Dashboard;
    county?: County.AppState;
    sos?: DOS.AppState;
    username?: string;
}

interface ContestAuditTypes {
    [contestId: number]: AuditType;
}

type CvrImportStatus
    = 'NOT_ATTEMPTED'
    | 'IN_PROGRESS'
    | 'SUCCESSFUL'
    | 'FAILED';

type AuditType
    = 'COMPARISON'
    | 'HAND_COUNT'
    | 'NOT_AUDITABLE'
    | 'NONE';

interface AuditBoardMember {
    firstName: string;
    lastName: string;
    party: string;
}

type AuditBoard = AuditBoardMember[];

interface AuditBoardStatus {
    members: AuditBoardMember[];
    signIn: Date;
}

interface Elector {
    firstName: string;
    lastName: string;
}

type RecordType
    = 'UPLOADED'
    | 'PHANTOM_RECORD'
    | 'AUDITOR_ENTERED'
    | 'PHANTOM_BALLOT';

interface Cvr {
    ballotType: string;  // String repr of number
    batchId: number;
    contestInfo: Array<{
        choices: string[];  // Name part of `ContestChoice`
        contest: number;
    }>;
    countyId: number;
    cvrNumber: number;
    id: number;
    imprintedId: string;  // `${scannerId}-${batchId}-${recordId}`
    recordId: number;
    recordType: RecordType;  // UPLOADED etc
    scannerId: number;
}

interface UploadedFile {
    approximateRecordCount: number;
    countyId: number;
    hash: string;
    hashStatus: string;
    id: number;
    name: string;
    size: number;
    status: string;
    timestamp: Date;
}

type AuditBoardASMState
    = 'AUDIT_INITIAL_STATE'
    | 'WAITING_FOR_ROUND_START'
    | 'WAITING_FOR_ROUND_START_NO_AUDIT_BOARD'
    | 'ROUND_IN_PROGRESS'
    | 'ROUND_IN_PROGRESS_NO_AUDIT_BOARD'
    | 'WAITING_FOR_ROUND_SIGN_OFF'
    | 'WAITING_FOR_ROUND_SIGN_OFF_NO_AUDIT_BOARD'
    | 'AUDIT_COMPLETE'
    | 'UNABLE_TO_AUDIT'
    | 'AUDIT_ABORTED';



interface CountyDashboardRefreshOkAction {
    type: 'COUNTY_DASHBOARD_REFRESH_OK';
    data: any;
}

interface CountyFetchContestsOkAction {
    type: 'COUNTY_FETCH_CONTESTS_OK';
    data: any;
}

interface CountyFetchCvrOkAction {
    type: 'COUNTY_FETCH_CVR_OK';
    data: any;
}

interface CountyLoginOk {
    type: 'COUNTY_LOGIN_OK';
    data: any;
}

interface DOSDashboardRefreshOkAction {
    type: 'DOS_DASHBOARD_REFRESH_OK';
    data: any;
}

interface DOSFetchContestsOkAction {
    type: 'DOS_FETCH_CONTESTS_OK';
    data: any;
}

interface DOSLoginOkAction {
    type: 'DOS_LOGIN_OK';
    data: any;
}

interface FetchAuditBoardASMStateOkAction {
    type: 'FETCH_AUDIT_BOARD_ASM_STATE_OK';
    data: any;
}

interface FetchCountyASMStateOkAction {
    type: 'FETCH_COUNTY_ASM_STATE_OK';
    data: JSON.FetchCountyASMStateOk;
}

interface FetchCvrsToAuditOkAction {
    type: 'FETCH_CVRS_TO_AUDIT_OK';
    data: any;
}

interface FetchDOSASMStateOkAction {
    type: 'FETCH_DOS_ASM_STATE_OK';
    data: any;
}

interface Login1FOkAction {
    type: 'LOGIN_1F_OK';
    data: any;
}

interface SelectContestsForAuditOkAction {
    type: 'SELECT_CONTESTS_FOR_AUDIT_OK';
    data: any;
}

interface UpdateAcvrFormAction {
    type: 'UPDATE_ACVR_FORM';
    data: any;
}

interface UploadBallotManifestOkAction {
    type: 'UPLOAD_BALLOT_MANIFEST_OK';
    data: any;
}

interface UploadAcvrOkAction {
    type: 'UPLOAD_ACVR_OK';
    data: any;
}

interface UploadCvrExportOkAction {
    type: 'UPLOAD_CVR_EXPORT_OK';
    data: any;
}

interface UploadRandomSeedOkAction {
    type: 'UPLOAD_RANDOM_SEED_OK';
    data: any;
}

interface UploadingBallotManifestAction {
    type: 'UPLOADING_BALLOT_MANIFEST';
    data: any;
}

interface UploadingCvrExportAction {
    type: 'UPLOADING_CVR_EXPORT';
    data: any;
}

type AppAction
    = CountyDashboardRefreshOkAction
    | CountyFetchContestsOkAction
    | CountyFetchCvrOkAction
    | CountyLoginOk
    | DOSDashboardRefreshOkAction
    | DOSFetchContestsOkAction
    | DOSLoginOkAction
    | FetchAuditBoardASMStateOkAction
    | FetchCountyASMStateOkAction
    | FetchCvrsToAuditOkAction
    | FetchDOSASMStateOkAction
    | Login1FOkAction
    | SelectContestsForAuditOkAction
    | UpdateAcvrFormAction
    | UploadBallotManifestOkAction
    | UploadAcvrOkAction
    | UploadCvrExportOkAction
    | UploadRandomSeedOkAction
    | UploadingBallotManifestAction
    | UploadingCvrExportAction;

type CountyDashboardStatus
    = 'NO_DATA'
    | 'CVRS_UPLOADED_SUCCESSFULLY'
    | 'ERROR_IN_UPLOADED_DATA';

type CVRImportStatus
    = 'NOT_ATTEMPTED'
    | 'IN_PROGRESS'
    | 'SUCCESSFUL'
    | 'FAILED';

interface Round {
    actualCount: number;
    disagreements: number;
    discrepancies: number;
    expectedCount: number;
    number: number;
    signatories: Elector[];
    startAuditPrefixLength: number;
    startIndex: number;
    startTime: Date;
}

interface ContestChoice {
    name: string;
    description: string;
}

interface Contest {
    choices: ContestChoice[];
    countyId: number;
    description: string;
    id: number;
    name: string;
    votesAllowed: number;
}

interface AuditedContest extends Contest {
    reason: AuditReason;
}

type AuditReason
    = 'STATE_WIDE_CONTEST'
    | 'COUNTY_WIDE_CONTEST'
    | 'CLOSE_CONTEST'
    | 'TIED_CONTEST'
    | 'GEOGRAPHICAL_SCOPE'
    | 'CONCERN_REGARDING_ACCURACY'
    | 'OPPORTUNISTIC_BENEFITS'
    | 'COUNTY_CLERK_ABILITY';

interface CountyInfo {
    id: number;
    name: string;
}

// TODO: Narrow type.
type OnClick = (...args: any[]) => any;

type Option<T> = T | null | undefined;
