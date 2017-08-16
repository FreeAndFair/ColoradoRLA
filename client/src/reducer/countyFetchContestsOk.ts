import * as _ from 'lodash';
const { merge } = _;


const parseChoices = (names: any, descriptions: any) => {
    return _.map(names, (name: any) => ({
        name,
        description: descriptions[name],
    }));
};

const parseDef = (c: any): any => {
    const { description, id, name } = c;

    const choices = parseChoices(c.choice_names, c.choice_descriptions);

    return { id, choices, description, name };
};

const parse = (data: any, state: any) => {
    const defs: any = {};

    data.forEach((c: any) => {
        defs[c.id] = parseDef(c);
    });

    return defs;
}


export default (state: any, action: any) => {
    const county = merge({}, state.county);

    county.contestDefs = merge({}, parse(action.data, state));

    return merge({}, state, { county });
};
