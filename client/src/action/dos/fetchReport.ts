import { endpoint } from '../../config';

import createFileFetchAction from '../createFileFetchAction';


const url = endpoint('state-report');


export default createFileFetchAction({
    failType: 'DOS_FETCH_REPORT_FAIL',
    networkFailType: 'DOS_FETCH_REPORT_NETWORK_FAIL',
    okType: 'DOS_FETCH_REPORT_OK',
    sendType: 'DOS_FETCH_REPORT_SEND',
    url,
});
