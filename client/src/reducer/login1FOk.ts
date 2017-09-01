function parse(data: any) {
    const dashboard = data.role === 'STATE'
        ? 'sos'
        : 'county';

    const loginChallenge: any = null;

    return { dashboard, loginChallenge };
}


export default (state: any, action: any) => ({
    ...state,
    ...parse(action.data.received),
});
