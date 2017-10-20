import * as _ from 'lodash';
const { merge } = _;


const parseDef = (c: any): any => {
    const { choices, description, id, name, votes_allowed } = c;
    const votesAllowed = votes_allowed;

    return { id, choices, description, name, votesAllowed };
};

const parse = (data: any, state: any) => {
    const defs: any = {};

    data.forEach((c: any) => {
        defs[c.id] = parseDef(c);
    });

    return defs;
};


export default function fetchContestsOk(
    state: County.AppState,
    action: Action.CountyFetchContestsOk,
): County.AppState {
    const nextState = { ...state };

    nextState.contestDefs = parse(action.data, state);

    return nextState;
}
