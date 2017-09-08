import * as _ from 'lodash';


const parseContest = (c: any) => {
    const result: any = _.omit(c, ['county_id', 'votes_allowed']);

    result.countyId = c.county_id;
    result.votesAllowed = c.votes_allowed;

    return result;
};

const byId = (o: any) => o.id;


export const parse = (contests: any) =>
    _.keyBy(_.map(contests, parseContest), byId);
