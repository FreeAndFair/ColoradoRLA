import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('hand-count');

const setHandCount = createSubmitAction({
    failType: 'SET_HAND_COUNT_FAIL',
    networkFailType: 'SET_HAND_COUNT_NETWORK_FAIL',
    okType: 'SET_HAND_COUNT_OK',
    sendType: 'SET_HAND_COUNT_SEND',
    url,
});

function format(id: number) {
    return [{
        audit: 'HAND_COUNT',
        contest: id,
        reason: 'OPPORTUNISTIC_BENEFITS',
    }];
}

export default (id: number) => setHandCount(format(id));
