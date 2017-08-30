import * as _ from 'lodash';


const parseContest = (c: any) => {
    const result: any = _.omit(c, ['county_id', 'votes_allowed']);

    result.countyId = c.county_id;
    result.votesAllowed = c.votes_allowed;

    return result;
};


export const parse = (contests: any) => contests.map(parseContest);
