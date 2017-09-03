import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('start-audit-round');


export default createSubmitAction({
    failType: 'PUBLISH_BALLOTS_TO_AUDIT_FAIL',
    networkFailType: 'PUBLISH_BALLOTS_TO_AUDIT_NETWORK_FAIL',
    okType: 'PUBLISH_BALLOTS_TO_AUDIT_OK',
    sendType: 'PUBLISH_BALLOTS_TO_AUDIT_SEND',
    url,
});
