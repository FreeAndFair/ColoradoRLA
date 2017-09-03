import { endpoint } from 'corla/config';

import createFetchAction from 'corla/action/createFetchAction';


export default (id: number) => {
    const e = endpoint('cvr/id');
    const url = `${e}/${id}`;

    const action = createFetchAction({
        failType: 'COUNTY_FETCH_CVR_FAIL',
        networkFailType: 'COUNTY_FETCH_CVR_NETWORK_FAIL',
        okType: 'COUNTY_FETCH_CVR_OK',
        sendType: 'COUNTY_FETCH_CVR_SEND',
        url,
    });

    action();
};
