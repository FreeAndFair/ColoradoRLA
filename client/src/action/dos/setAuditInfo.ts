import { get, isNil, omitBy } from 'lodash';

import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('update-audit-info');

const setAuditInfo = createSubmitAction({
    failType: 'SET_AUDIT_INFO_FAIL',
    networkFailType: 'SET_AUDIT_INFO_NETWORK_FAIL',
    okType: 'SET_AUDIT_INFO_OK',
    sendType: 'SET_AUDIT_INFO_SEND',
    url,
});

function format(info: DOS.AuditInfo) {
    const data = {
        election_date: get(info, 'election.date'),
        election_type: get(info, 'election.type'),
        public_meeting_date: info.publicMeetingDate,
        risk_limit: info.riskLimit,
    };

    return omitBy(data, isNil);
}


export default (info: DOS.AuditInfo) => setAuditInfo(format(info));
