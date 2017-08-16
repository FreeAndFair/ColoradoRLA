import { Dispatch } from 'redux';

import { endpoint } from '../config';

import createFetchAction from './createFetchAction';


export default (id: number) => (dispatch: Dispatch<any>) => {
    const e = endpoint('contest/county')
    const url = `${e}?${id}`;

    const action = createFetchAction({
        failType: 'DOS_FETCH_CONTESTS_FAIL',
        networkFailType: 'DOS_FETCH_CONTESTS_NETWORK_FAIL',
        okType: 'DOS_FETCH_CONTESTS_OK',
        sendType: 'DOS_FETCH_CONTESTS_SEND',
        url,
    });

    action()(dispatch);
};
