import { endpoint } from 'corla/config';

import createSubmitAction from './createSubmitAction';

import { format } from 'corla/adapter/contestsForAudit';


const url = endpoint('select-contests');

const selectContestsForAudit = createSubmitAction({
    failType: 'SELECT_CONTESTS_FOR_AUDIT_FAIL',
    networkFailType: 'SELECT_CONTESTS_FOR_AUDIT_NETWORK_FAIL',
    okType: 'SELECT_CONTESTS_FOR_AUDIT_OK',
    sendType: 'SELECT_CONTESTS_FOR_AUDIT_SEND',
    url,
});


export default (data: any) =>
    selectContestsForAudit(format(data));
