type Dashboard = 'sos' | 'county';

type ElectionType = 'coordinated'
                  | 'primary'
                  | 'general'
                  | 'recall';

interface AppState {
    loginChallenge: any;
    dashboard?: Dashboard;
    county?: CountyState;
    sos?: DosState;
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
    audit_cvr: {
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
    };
    cvr_id: number;
}

interface Cvr {
    ballotType: any;
    batchId: any;
    countyId: any;
    cvrNumber: any;
    id: any;
    imprintedId: any;
    recordId: any;
    recordType: any;
    scannerId: any;
}

interface UploadFileOkJson {
    approximate_record_count: number;
    county_id: number;
    file_id: number;
    hash: string;
    hash_status: string;
    status: string;
    size: number;
    timestamp: string;
}

type UploadBallotManifestOkJson = UploadFileOkJson;

type UploadCvrExportOkJson = UploadFileOkJson;
