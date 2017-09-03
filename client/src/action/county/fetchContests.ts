import { endpoint } from 'corla/config';

import createFetchAction from 'corla/action/createFetchAction';


export default (id: number) => {
    const e = endpoint('contest/county');
    const url = `${e}?${id}`;

    const action = createFetchAction({
        failType: 'COUNTY_FETCH_CONTESTS_FAIL',
        networkFailType: 'COUNTY_FETCH_CONTESTS_NETWORK_FAIL',
        okType: 'COUNTY_FETCH_CONTESTS_OK',
        sendType: 'COUNTY_FETCH_CONTESTS_SEND',
        url,
    });

    action();
};
