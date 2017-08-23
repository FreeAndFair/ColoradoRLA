export default (state: any) => ({
    ...state,
    county: {
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
