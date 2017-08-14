import { Dispatch } from 'redux';

import { apiHost } from '../config';

import createSubmitAction from './createSubmitAction';


const url = `http://${apiHost}/upload-audit-cvr`;

const uploadAuditCVRs = createSubmitAction({
    failType: 'UPLOAD_AUDIT_CVR_FAIL',
    networkFailType: 'UPLOAD_AUDIT_CVR_NETWORK_FAIL',
    okType: 'UPLOAD_AUDIT_CVR_OK',
    sendType: 'UPLOAD_AUDIT_CVR_SEND',
    url,
});


export default uploadAuditCVRs;
