export default (state: any) => ({
    ...state,
    county: { auditBoard: [], contests: {} },
    dashboard: 'county',
    loggedIn: true,
});
