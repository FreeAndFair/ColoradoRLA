import { Dispatch } from 'redux';

import { apiHost } from '../config';


const uploadCvrExport = (countyId: number, file: Blob, hash: string) => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: 'UPLOAD_CVR_EXPORT_SEND' });

        const url = `http://${apiHost}/upload-cvr-export`;

        const formData = new FormData();
        formData.append('county', `${countyId}`);
        formData.append('cvr_file', file);
        formData.append('hash', hash);

        const init: any = {
            body: formData,
            credentials: 'include',
            method: 'post',
        };

        fetch(url, init)
            .then(r => {
                if (r.ok) {
                    dispatch({ type: 'UPLOAD_CVR_EXPORT_OK' });
                } else {
                    dispatch({ type: 'UPLOAD_CVR_EXPORT_FAIL' });
                }
            })
            .catch(() => {
                dispatch({ type: 'UPLOAD_CVR_EXPORT_NETWORK_FAIL' });
            });
    };
};


export default uploadCvrExport;
