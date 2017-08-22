import { endpoint } from '../config';

import createSubmitAction from './next/createSubmitAction';


const url = endpoint('ballots-to-audit/publish');


export default createSubmitAction({
    failType: 'PUBLISH_BALLOTS_TO_AUDIT_FAIL',
    networkFailType: 'PUBLISH_BALLOTS_TO_AUDIT_NETWORK_FAIL',
    okType: 'PUBLISH_BALLOTS_TO_AUDIT_OK',
    sendType: 'PUBLISH_BALLOTS_TO_AUDIT_SEND',
    url,
});
