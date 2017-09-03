import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('start-audit-round');

const startNextRound = createSubmitAction({
    failType: 'DOS_START_NEXT_ROUND_FAIL',
    networkFailType: 'DOS_START_NEXT_ROUND_NETWORK_FAIL',
    okType: 'DOS_START_NEXT_ROUND_OK',
    sendType: 'DOS_START_NEXT_ROUND_SEND',
    url,
});


export default () => startNextRound({
    multiplier: 1,
    use_estimates: true,
});
