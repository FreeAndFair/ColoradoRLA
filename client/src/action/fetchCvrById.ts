import { Dispatch } from 'redux';

import { endpoint } from '../config';

import createFetchAction from './createFetchAction';


export default (id: number) => (dispatch: Dispatch<any>) => {
    const e = endpoint('cvr/id');
    const url = `${e}/${id}`;

    const action = createFetchAction({
        failType: 'COUNTY_FETCH_CVR_BY_ID_FAIL',
        networkFailType: 'COUNTY_FETCH_CVR_BY_ID_NETWORK_FAIL',
        okType: 'COUNTY_FETCH_CVR_BY_ID_OK',
        sendType: 'COUNTY_FETCH_CVR_BY_ID_SEND',
        url,
    });

    action()(dispatch);
};
