import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('ballot-not-found');

const ballotNotFound = createSubmitAction({
    failType: 'BALLOT_NOT_FOUND_FAIL',
    networkFailType: 'BALLOT_NOT_FOUND_NETWORK_FAIL',
    okType: 'BALLOT_NOT_FOUND_OK',
    sendType: 'BALLOT_NOT_FOUND_SEND',
    url,
});


export default (id: number) => ballotNotFound({ id });
