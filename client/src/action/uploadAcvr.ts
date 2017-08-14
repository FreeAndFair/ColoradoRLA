import { Dispatch } from 'redux';

import { apiHost } from '../config';

import createSubmitAction from './createSubmitAction';


const url = `http://${apiHost}/upload-audit-cvr`;

const uploadAcvr = createSubmitAction({
    failType: 'UPLOAD_ACVR_FAIL',
    networkFailType: 'UPLOAD_ACVR_NETWORK_FAIL',
    okType: 'UPLOAD_ACVR_OK',
    sendType: 'UPLOAD_ACVR_SEND',
    url,
});


export default uploadAcvr;
