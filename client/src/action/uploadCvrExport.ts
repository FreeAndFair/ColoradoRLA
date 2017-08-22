import { endpoint } from '../config';

import createFileUploadAction from './createFileUploadAction';


const url = endpoint('upload-cvr-export');


function createFormData(countyId: number, file: Blob, hash: string) {
    const formData = new FormData();

    formData.append('county', `${countyId}`);
    formData.append('cvr_file', file);
    formData.append('hash', hash);

    return formData;
}

function createSent(countyId: number, file: Blob, hash: string) {
    return { countyId, file, hash };
}


export default createFileUploadAction({
    createFormData,
    createSent,
    failType: 'UPLOAD_CVR_EXPORT_FAIL',
    networkFailType: 'UPLOAD_CVR_EXPORT_NETWORK_FAIL',
    okType: 'UPLOAD_CVR_EXPORT_OK',
    sendType: 'UPLOAD_CVR_EXPORT_SEND',
    url,
});
