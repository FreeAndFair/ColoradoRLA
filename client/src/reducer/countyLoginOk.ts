import * as cookies from 'js-cookie';


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
    session: cookies.get('JSESSIONID'),
});
