import { endpoint } from '../config';

import createSubmitAction from './createSubmitAction';


const url = endpoint('sign-off-audit-round');

const roundSignOff = createSubmitAction({
    failType: 'SUBMIT_ROUND_SIGN_OFF_FAIL',
    networkFailType: 'SUBMIT_ROUND_SIGN_OFF_NETWORK_FAIL',
    okType: 'SUBMIT_ROUND_SIGN_OFF_OK',
    sendType: 'SUBMIT_ROUND_SIGN_OFF_SEND',
    url,
});


function format(electors: any[]) {
    return electors.map(e => ({
        first_name: e.firstName,
        last_name: e.lastName,
    }));
}


export default (electors: any) => roundSignOff(format(electors));
