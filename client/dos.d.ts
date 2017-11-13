declare namespace DOS {
    interface AppState {
        asm: DOS.ASMState;
        auditedContests: DOS.AuditedContests;
        auditTypes: ContestAuditTypes;
        contests: DOS.Contests;
        countyStatus: DOS.CountyStatuses;
        discrepancyCounts?: DOS.DiscrepancyCounts;
        election?: Election;
        publicMeetingDate?: Date;
        riskLimit?: number;
        seed?: string;
        type: 'DOS';
    }

    interface DiscrepancyCount {
        audited: number;
        unaudited: number;
    }

    interface DiscrepancyCounts {
        [countyId: number]: DOS.DiscrepancyCount;
    }

    type DiscrepancyType = '-2' | '-1' | '0' | '1' | '2';

    interface DiscrepancyCount {
        // The index type is really limited to `DiscrepancyType`.
        [type: string]: number;
    }

    interface CountyStatuses {
        [countyId: number]: DOS.CountyStatus;
    }

    interface CountyStatus {
        asmState: County.ASMState;
        auditBoard: AuditBoardStatus;
        auditBoardASMState: AuditBoardASMState;
        auditedBallotCount: number;
        ballotManifest: UploadedFile;
        ballotsRemainingInRound: number;
        currentRound: Round;
        cvrExport: UploadedFile;
        disagreementCount: number;
        discrepancyCount: DiscrepancyCount;
        estimatedBallotsToAudit: number;
        id: number;
        manifestTimestamp: any;
        rounds: any;
        status: any;
    }

    interface Contests {
        [contestId: number]: Contest;
    }

    interface AuditedContests {
        [contestId: number]: AuditedContest;
    }

    type ASMState
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

    interface AuditInfo {
        election: Election;
        publicMeetingDate: Date;
        riskLimit: number;
    }

    namespace Form {
        namespace AuditDef {
            interface Forms {
                electionDateForm?: DOS.Form.ElectionDate.Form;
                electionTypeForm?: DOS.Form.ElectionType.Form;
                publicMeetingDateForm?: DOS.Form.PublicMeetingDate.Form;
                riskLimit?: DOS.Form.RiskLimit.Form;
            }
        }

        namespace ElectionDate {
            interface Form {
                date: string;
            }
        }

        namespace ElectionType {
            interface Form {
                type?: ElectionType;
            }
        }

        namespace PublicMeetingDate {
            interface Form {
                date: string;
            }
        }

        namespace RiskLimit {
            interface Form {
                ballotPollingField: string;
                ballotPollingLimit: number;
                comparisonField: string;
                comparisonLimit: number;
            }
        }

        namespace Seed {
            interface Ref {
                seedForm?: DOS.Form.Seed.Form;
            }

            interface Form {
                seed: string;
            }
        }

        namespace SelectContests {
            interface Ref {
                selectContestsForm?: DOS.Form.SelectContests.FormData;
            }

            type ReasonId = 'county_wide_contest' | 'state_wide_contest';

            interface Reason {
                id: ReasonId;
                text: string;
            }

            interface ContestStatus {
                audit: boolean;
                handCount: boolean;
                reason: Reason;
            }

            interface FormData {
                [contestId: number]: ContestStatus;
            }
        }
    }
}
