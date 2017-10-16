type Dashboard = 'sos' | 'county';

type ElectionType = 'coordinated'
                  | 'primary'
                  | 'general'
                  | 'recall';

interface AppState {
    loginChallenge: any;
    dashboard?: Dashboard;
    county?: CountyState;
    sos?: any;
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
