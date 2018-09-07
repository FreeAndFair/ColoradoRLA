import { endpoint } from 'corla/config';

import { format } from 'corla/adapter/standardizeContests';

import createSubmitAction from 'corla/action/createSubmitAction';

const url = endpoint('set-contest-names');

const standardizeContestsForAudit = createSubmitAction({
    failType: 'STANDARDIZE_CONTESTS_FOR_AUDIT_FAIL',
    networkFailType: 'STANDARDIZE_CONTESTS_FOR_AUDIT_NETWORK_FAIL',
    okType: 'STANDARDIZE_CONTESTS_FOR_AUDIT_OK',
    sendType: 'STANDARDIZE_CONTESTS_FOR_AUDIT_SEND',
    url,
});


export default (data: DOS.Form.StandardizeContests.FormData) =>
    standardizeContestsForAudit(format(data));
