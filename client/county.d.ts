declare namespace County {
    interface AppState {
        acvrs?: Acvrs;
        asm?: any;
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
        rounds?: Round[];
        type: 'County';
        uploadingBallotManifest?: boolean;
        uploadingCvrExport?: boolean;
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

    interface ASM {
        currentState: ASMState;
        enabledUiEvents: string[];
    }

    type ASMState
        = 'COUNTY_INITIAL_STATE'
        | 'COUNTY_AUTHENTICATED'
        | 'BALLOT_MANIFEST_OK'
        | 'CVRS_OK'
        | 'BALLOT_MANIFEST_AND_CVRS_OK'
        | 'COUNTY_AUDIT_UNDERWAY'
        | 'COUNTY_AUDIT_COMPLETE'
        | 'DEADLINE_MISSED';
}
