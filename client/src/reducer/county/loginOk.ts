export default (state: any) => ({
    ...state,
    county: {
        acvrs: {},
        asm: {
            auditBoard: {},
            county: {},
        },
        auditBoard: [],
        contests: {},
    },
    dashboard: 'county',
    loggedIn: true,
});
