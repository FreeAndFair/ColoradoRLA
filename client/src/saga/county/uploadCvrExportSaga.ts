import {
    put,
    select,
    takeLatest,
} from 'redux-saga/effects';

import notice from 'corla/notice';


function* importCvrExportFail(action: any): IterableIterator<any> {
    const { data } = action;
    const { received, sent } = data;

    notice.danger('Failed to import CVR export.');

    if (sent.hash_status === 'MISMATCH') {
        notice.warning('Please verify that the hash matches the file to be uploaded.');
        return null;
    }

    if (received.result && received.result.includes('prohibited header CountingGroup')) {
        notice.danger('The CVR export contained the prohibited "CountingGroup" column.', 10000);
        return null;
    }

    notice.warning('Please verify that the uploaded file is a valid CVR export.');

    return null;
}

function* importCvrExportNetworkFail(): IterableIterator<any> {
    notice.danger('Network error: failed to upload CVR export.');
}

function* uploadCvrExportFail(action: any): IterableIterator<any> {
    notice.danger('Failed to upload CVR export.');
}

function* uploadCvrExportNetworkFail(): IterableIterator<any> {
    notice.danger('Network error: failed to upload CVR export.');
}

function createUploadingCvrExport(uploading: boolean) {
    function* uploadingCvrExport(action: any): IterableIterator<any> {
        const data = { uploading };

        yield put({ data, type: 'UPLOADING_CVR_EXPORT' });
    }

    return uploadingCvrExport;
}

const UPLOADING_FALSE = [
    'IMPORT_CVR_EXPORT_FAIL',
    'IMPORT_CVR_EXPORT_NETWORK_FAIL',
    'IMPORT_CVR_EXPORT_OK',
    'UPLOAD_CVR_EXPORT_FAIL',
    'UPLOAD_CVR_EXPORT_NETWORK_FAIL',
];

const UPLOADING_TRUE = [
    'UPLOAD_CVR_EXPORT_SEND',
];


export default function* uploadCvrExportSaga() {
    yield takeLatest('IMPORT_CVR_EXPORT_FAIL', importCvrExportFail);
    yield takeLatest('IMPORT_CVR_EXPORT_NETWORK_FAIL', importCvrExportNetworkFail);

    yield takeLatest('UPLOAD_CVR_EXPORT_FAIL', uploadCvrExportFail);
    yield takeLatest('UPLOAD_CVR_EXPORT_NETWORK_FAIL', uploadCvrExportNetworkFail);

    yield takeLatest(UPLOADING_FALSE, createUploadingCvrExport(false));
    yield takeLatest(UPLOADING_TRUE, createUploadingCvrExport(true));
}
