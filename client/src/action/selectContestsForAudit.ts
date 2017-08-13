import { Dispatch } from 'redux';

import { apiHost } from '../config';

import createSubmitAction from './createSubmitAction';


const url = `http://${apiHost}/select-contests`;

const selectContestsForAudit = createSubmitAction({
    failType: 'SELECT_CONTESTS_FOR_AUDIT_FAIL',
    networkFailType: 'SELECT_CONTESTS_FOR_AUDIT_NETWORK_FAIL',
    okType: 'SELECT_CONTESTS_FOR_AUDIT_OK',
    sendType: 'SELECT_CONTESTS_FOR_AUDIT_SEND',
    url,
});


export default selectContestsForAudit;
