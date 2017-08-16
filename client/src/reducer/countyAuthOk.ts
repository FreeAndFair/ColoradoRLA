export default (state: any) => ({
    ...state,
    county: { contests: {} },
    dashboard: 'county',
    loggedIn: true,
});
