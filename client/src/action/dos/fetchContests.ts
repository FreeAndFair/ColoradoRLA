import { endpoint } from 'corla/config';

import createFetchAction from 'corla/action/createFetchAction';


const url = endpoint('contest');


export default createFetchAction({
    failType: 'DOS_FETCH_CONTESTS_FAIL',
    networkFailType: 'DOS_FETCH_CONTESTS_NETWORK_FAIL',
    okType: 'DOS_FETCH_CONTESTS_OK',
    sendType: 'DOS_FETCH_CONTESTS_SEND',
    url,
});
