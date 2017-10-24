declare namespace County {
    interface AppState {
        acvrs?: Acvrs;
        asm: ASMStates;
        auditBoard: AuditBoard;
        auditedBallotCount?: number;
        ballotManifest?: UploadedFile;
        ballotManifestCount?: number;
        ballotManifestHash?: string;
        ballotsRemainingInRound?: number;
        contests?: Contest[];
        contestDefs?: ContestDefs;
        contestsUnderAudit?: Contest[];
        currentBallot?: Cvr;
        currentRound?: Option<Round>;
        cvrExport?: UploadedFile;
        cvrExportCount?: number;
        cvrExportHash?: string;
        cvrImportStatus?: CvrImportStatus;
        cvrsToAudit?: JSON.Cvr[];  // Sic
        disagreementCount?: number;
        discrepancyCount?: number;
        election?: Election;
        estimatedBallotsToAudit?: number;
        fileName?: string;  // TODO: remove
        hash?: string;  // TODO: remove
        id?: number;
        riskLimit?: number;
        rounds: Round[];
        type: 'County';
        uploadingBallotManifest?: boolean;
        uploadingCvrExport?: boolean;
    }

    interface ASMStates {
        auditBoard: AuditBoardASMState;
        county: ASMState;
    }

    interface Acvrs {
        [cvrId: number]: Acvr;
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

    interface ContestDefs {
        [id: number]: Contest;
    }

    type ASMState
        = 'COUNTY_INITIAL_STATE'
        | 'BALLOT_MANIFEST_OK'
        | 'CVRS_IMPORTING'
        | 'CVRS_OK'
        | 'BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING'
        | 'BALLOT_MANIFEST_AND_CVRS_OK'
        | 'COUNTY_AUDIT_UNDERWAY'
        | 'COUNTY_AUDIT_COMPLETE'
        | 'DEADLINE_MISSED';
}
