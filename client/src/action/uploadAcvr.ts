import { Dispatch } from 'redux';

import { endpoint } from '../config';

import createSubmitAction from './createSubmitAction';

import { format } from '../adapter/uploadAcvr';


const url = endpoint('upload-audit-cvr');

const uploadAcvr = createSubmitAction({
    failType: 'UPLOAD_ACVR_FAIL',
    networkFailType: 'UPLOAD_ACVR_NETWORK_FAIL',
    okType: 'UPLOAD_ACVR_OK',
    sendType: 'UPLOAD_ACVR_SEND',
    url,
});


export default (marks: any, cvr: any) => uploadAcvr(format(marks, cvr));
