import { endpoint } from '../config';

import createFetchAction from './next/createFetchAction';


const url = endpoint('contest');


export default createFetchAction({
    failType: 'DOS_FETCH_CONTESTS_FAIL',
    networkFailType: 'DOS_FETCH_CONTESTS_NETWORK_FAIL',
    okType: 'DOS_FETCH_CONTESTS_OK',
    sendType: 'DOS_FETCH_CONTESTS_SEND',
    url,
});
