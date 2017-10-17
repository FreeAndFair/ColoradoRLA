type Dashboard = 'sos' | 'county';

type ElectionType = 'coordinated'
                  | 'primary'
                  | 'general'
                  | 'recall';

type LoginChallengeBox = [string, string];

type LoginChallenge = LoginChallengeBox[];

interface AppState {
    loginChallenge: LoginChallenge;
    dashboard?: Dashboard;
    county?: CountyState;
    sos?: DosState;
    username?: string;
}

interface CountyState {
    acvrs: any;
    asm: any;
    auditBoard: any;
    contests: any;
    contestDefs?: any;
    currentBallot?: any;
    currentRound?: any;
    cvrsToAudit?: any;
    fileName?: any;  // TODO: remove
    hash?: any;  // TODO: remove
    uploadingBallotManifest?: boolean;
    uploadingCvrExport?: boolean;
}

interface DosState {
    asm: any;
    auditStage: any;
    auditedContests: any;
    contests?: any;
    contestsForAudit?: any;
    countyStatus: any;
    seed?: any;
}

interface AuditBoardMember {
    firstName: string;
    lastName: string;
    party: string;
}

type AuditBoard = AuditBoardMember[];

interface AuditBoardMemberJson {
    first_name: string;
    last_name: string;
    political_party: string;
}

type AuditBoardJson = AuditBoardMemberJson[];

interface RiskLimitJson {
    risk_limit: number;
}

interface Elector {
    firstName: string;
    lastName: string;
}

interface ElectorJson {
    first_name: string;
    last_name: string;
}

interface Acvr {
    [contestId: number]: AcvrContest;
}

interface AcvrContest {
    choices: AcvrChoices;
    comments: string;
    noConsensus: boolean;
}

interface AcvrChoices {
    [contestChoice: string]: boolean;
}

interface ContestInfoJson {
    choices: string[];
    contest: number;
    consensus: string;
}

interface AcvrJson {
    audit_cvr: CvrJson;
    cvr_id: number;
}

interface Cvr {
    ballotType: any;
    batchId: any;
    contestInfo: any;
    countyId: any;
    cvrNumber: any;
    id: any;
    imprintedId: any;
    recordId: any;
    recordType: any;
    scannerId: any;
}

interface CvrJson {
    ballot_type: string;
    batch_id: string;
    contest_info: ContestInfoJson[];
    county_id: number;
    cvr_number: number;
    id: number;
    imprinted_id: string;
    record_id: string;
    record_type: string;
    scanner_id: string;
    timestamp: Date;
}

interface UploadedFileJson {
    approximate_record_count: number;
    county_id: number;
    file_id: number;
    filename: string;
    hash: string;
    hash_status: string;
    status: string;
    size: number;
    timestamp: string;
}

type UploadFileOkJson = UploadedFileJson;

type UploadBallotManifestOkJson = UploadFileOkJson;

type UploadCvrExportOkJson = UploadFileOkJson;

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

interface FetchCountyAsmStateOkJson {
    current_state: CountyAsmState;
    enabled_ui_events: string[];
}

interface FetchDosAsmStateOkJson {
    current_state: DosAsmState;
    enabled_ui_events: string[];
}

interface CountyAsm {
    currentState: CountyAsmState;
    enabledUiEvents: string[];
}

interface DosAsm {
    currentState: DosAsmState;
    enabledUiEvents: string[];
}

type AuditBoardAsmState
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

type CountyAsmState
    = 'COUNTY_INITIAL_STATE'
    | 'COUNTY_AUTHENTICATED'
    | 'BALLOT_MANIFEST_OK'
    | 'CVRS_OK'
    | 'BALLOT_MANIFEST_AND_CVRS_OK'
    | 'COUNTY_AUDIT_UNDERWAY'
    | 'COUNTY_AUDIT_COMPLETE'
    | 'DEADLINE_MISSED';

type DosAsmState
    = 'DOS_INITIAL_STATE'
    | 'DOS_AUTHENTICATED'
    | 'RISK_LIMITS_SET'
    | 'CONTESTS_TO_AUDIT_IDENTIFIED'
    | 'DATA_TO_AUDIT_PUBLISHED'
    | 'RANDOM_SEED_PUBLISHED'
    | 'BALLOT_ORDER_DEFINED'
    | 'AUDIT_READY_TO_START'
    | 'DOS_AUDIT_ONGOING'
    | 'DOS_ROUND_COMPLETE'
    | 'DOS_AUDIT_COMPLETE'
    | 'AUDIT_RESULTS_PUBLISHED';

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

interface DosDashboardRefreshOkAction {
    type: 'DOS_DASHBOARD_REFRESH_OK';
    data: any;
}

interface DosFetchContestsOkAction {
    type: 'DOS_FETCH_CONTESTS_OK';
    data: any;
}

interface DosLoginOkAction {
    type: 'DOS_LOGIN_OK';
    data: any;
}

interface FetchAuditBoardAsmStateOkAction {
    type: 'FETCH_AUDIT_BOARD_ASM_STATE_OK';
    data: any;
}

interface FetchCountyAsmStateOkAction {
    type: 'FETCH_COUNTY_ASM_STATE_OK';
    data: FetchCountyAsmStateOkJson;
}

interface FetchCvrsToAuditOkAction {
    type: 'FETCH_CVRS_TO_AUDIT_OK';
    data: any;
}

interface FetchDosAsmStateOkAction {
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
    | DosDashboardRefreshOkAction
    | DosFetchContestsOkAction
    | DosLoginOkAction
    | FetchAuditBoardAsmStateOkAction
    | FetchCountyAsmStateOkAction
    | FetchCvrsToAuditOkAction
    | FetchDosAsmStateOkAction
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
    signatories: any;
    startAuditPrefixLength: number;
    startIndex: number;
    startTime: Date;
}

interface RoundJson {
    actual_count: number;
    disagreements: number;
    discrepancies: number;
    expected_count: number;
    number: number;
    signatories: any;
    start_audit_prefix_length: number;
    start_index: number;
    start_time: Date;
}

interface CountyDashboardJson {
    asm_state: string;
    audit_board: any;
    audit_info: any;
    audit_time: string;
    audited_ballot_count: number;
    audited_prefix_length: number;
    ballot_manifest_count: number;
    ballot_manifest_file: any;
    ballot_under_audit_id: number;
    ballots_remaining_in_round: number;
    current_round: RoundJson;
    cvr_export_count: number;
    cvr_export_file: any;
    cvr_import_status: CVRImportStatus;
    contests: number[];
    contests_under_audit: number[];
    disagreement_count: number;
    discrepancy_count: number;
    estimated_ballots_to_audit: number;
    general_information: string;
    id: number;
    risk_limit: number;
    rounds: RoundJson[];
    status: CountyDashboardStatus;
}
