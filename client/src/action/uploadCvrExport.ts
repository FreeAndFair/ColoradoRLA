import { Dispatch } from 'redux';

import { apiHost } from '../config';


const uploadCvrExport = (countyId: string, file: Blob, hash: string) => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: 'UPLOAD_CVR_EXPORT_SEND' });

        const url = `http://${apiHost}/upload-cvr-export`;

        const formData = new FormData();
        formData.append('county', countyId);
        formData.append('cvr_file', file);
        formData.append('hash', hash);

        fetch(url, { method: 'post', body: formData })
            .then(r => {
                if (r.ok) {
                    dispatch({ type: 'UPLOAD_CVR_EXPORT_RECEIVE' });
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
