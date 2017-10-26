export function countyState(): County.AppState {
    return {
        acvrs: {},
        asm: {
            auditBoard: 'AUDIT_INITIAL_STATE',
            county: 'COUNTY_INITIAL_STATE',
        },
        auditBoard: [],
        contests: [],
        cvrImportPending: {
            alerted: false,
            started: new Date(),
        },
        cvrImportStatus: {
            state: 'NOT_ATTEMPTED',
            timestamp: new Date(),
        },
        rounds: [],
        type: 'County',
    };
}

export function dosState(): DOS.AppState {
    return {
        asm: 'DOS_INITIAL_STATE',
        auditTypes: {},
        auditedContests: {},
        contests: {},
        countyStatus: {},
        type: 'DOS',
    };
}

export function loginState(): LoginAppState {
    return { type: 'Login' };
}


export default {
    county: countyState,
    dos: dosState,
    login: loginState,
};
