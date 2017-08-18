import * as _ from 'lodash';


const parseContest = (c: any) => {
    const result: any = _.omit(c, 'votes_allowed');

    result.votesAllowed = c.votes_allowed;

    return result;
};


export const parse = (contests: any) => contests.map(parseContest);
