import { Dispatch } from 'redux';

import { endpoint } from '../config';

import createFetchAction from './createFetchAction';


export default (id: number) => (dispatch: Dispatch<any>) => {
    const e = endpoint('cvr/county');
    const url = `${e}?${id}`;

    const action = createFetchAction({
        failType: 'COUNTY_FETCH_ALL_CVRS_FAIL',
        networkFailType: 'COUNTY_FETCH_ALL_CVRS_NETWORK_FAIL',
        okType: 'COUNTY_FETCH_ALL_CVRS_OK',
        sendType: 'COUNTY_FETCH_ALL_CVRS_SEND',
        url,
    });

    action()(dispatch);
};
