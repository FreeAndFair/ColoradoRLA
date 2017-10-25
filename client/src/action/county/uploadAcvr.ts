import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';

import { format } from 'corla/adapter/uploadAcvr';


const url = endpoint('upload-audit-cvr');

const uploadAcvr = createSubmitAction({
    failType: 'UPLOAD_ACVR_FAIL',
    networkFailType: 'UPLOAD_ACVR_NETWORK_FAIL',
    okType: 'UPLOAD_ACVR_OK',
    sendType: 'UPLOAD_ACVR_SEND',
    url,
});


export default (acvr: County.ACVR, cvr: CVR) => uploadAcvr(format(acvr, cvr));
