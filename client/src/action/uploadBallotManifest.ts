import { endpoint } from '../config';

import createFileUploadAction from './createFileUploadAction';


const url = endpoint('upload-ballot-manifest');


function createFormData(countyId: number, file: Blob, hash: string) {
    const formData = new FormData();

    formData.append('county', `${countyId}`);
    formData.append('bmi_file', file);
    formData.append('hash', hash);

    return formData;
}

function createSent(countyId: number, file: Blob, hash: string) {
    return { countyId, file, hash };
}


export default createFileUploadAction({
    createFormData,
    createSent,
    failType: 'UPLOAD_BALLOT_MANIFEST_FAIL',
    networkFailType: 'UPLOAD_BALLOT_MANIFEST_NETWORK_FAIL',
    okType: 'UPLOAD_BALLOT_MANIFEST_OK',
    sendType: 'UPLOAD_BALLOT_MANIFEST_SEND',
    url,
});
