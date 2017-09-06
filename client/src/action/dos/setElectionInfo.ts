import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('set-election-info');

const setRiskLimit = createSubmitAction({
    failType: 'SET_ELECTION_INFO_FAIL',
    networkFailType: 'SET_ELECTION_INFO_NETWORK_FAIL',
    okType: 'SET_ELECTION_INFO_OK',
    sendType: 'SET_ELECTION_INFO_SEND',
    url,
});

function format(date: Date, type: string) {
    return {
        election_date: date,
        election_type: type,
    };
}


export default (date: Date, type: string) => setRiskLimit(format(date, type));
