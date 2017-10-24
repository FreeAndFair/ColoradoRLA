export function countyState(): County.AppState {
    return {
        acvrs: {},
        asm: {
            auditBoard: {},
            county: {},
        },
        auditBoard: [],
        contests: [],
        type: 'County',
    };
}

export function dosState(): DOS.AppState {
    return {
        asm: { currentState: 'DOS_INITIAL_STATE' },
        auditedContests: {},
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
