type Dashboard = 'DOS' | 'County';

interface Election {
    date: Date;
    type: ElectionType;
}

type ElectionType
    = 'coordinated'
    | 'primary'
    | 'general'
    | 'recall';

type AuthRole = 'STATE' | 'COUNTY';

type AuthStage
    = 'NOT_AUTHENTICATED'
    | 'TRADITIONALLY_AUTHENTICATED'
    | 'SECOND_FACTOR_AUTHENTICATED';

type LoginChallengeBox = [string, string];

type LoginChallenge = LoginChallengeBox[];

type AppStateType = 'County' | 'DOS' | 'Login';

interface LoginAppState {
    dashboard?: Dashboard;
    loginChallenge?: Option<LoginChallenge>;
    username?: string;
    type: 'Login';
}

type AppState = LoginAppState | County.AppState | DOS.AppState;

interface ContestAuditTypes {
    [contestId: number]: AuditType;
}

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

interface AuditBoards {
    [index: number]: AuditBoardStatus;
}

interface Elector {
    firstName: string;
    lastName: string;
}

interface Signatories {
    [index: number]: Elector[];
}

type RecordType
    = 'UPLOADED'
    | 'PHANTOM_RECORD'
    | 'AUDITOR_ENTERED'
    | 'PHANTOM_BALLOT';

interface CVR {
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

type CountyDashboardStatus
    = 'NO_DATA'
    | 'CVRS_UPLOADED_SUCCESSFULLY'
    | 'ERROR_IN_UPLOADED_DATA';

interface Round {
    actualCount: number;
    disagreements: number;
    discrepancies: number;
    expectedCount: number;
    number: number;
    signatories: Signatories;
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

interface AuditedContest {
    id: number;
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

type Select<P> = (state: AppState) => P;

type Bind<P, S> = (dispatch: Dispatch<S>) => P;
