import { Dispatch } from 'redux';

import { endpoint } from '../config';

import createSubmitAction from './createSubmitAction';

import { format } from '../adapter/uploadAcvr';


const url = endpoint('ballot-not-found');

const ballotNotFound = createSubmitAction({
    failType: 'BALLOT_NOT_FOUND_FAIL',
    networkFailType: 'BALLOT_NOT_FOUND_NETWORK_FAIL',
    okType: 'BALLOT_NOT_FOUND_OK',
    sendType: 'BALLOT_NOT_FOUND_SEND',
    url,
});


export default (id: any) => ballotNotFound({ id });
