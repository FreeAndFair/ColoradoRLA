import { Dispatch } from 'redux';

import { apiHost } from '../config';


const uploadBallotManifest = (countyId: number, file: Blob, hash: string) => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: 'UPLOAD_BALLOT_MANIFEST_SEND' });

        const url = `http://${apiHost}/upload-ballot-manifest`;

        const formData = new FormData();
        formData.append('county', `${countyId}`);
        formData.append('bmi_file', file);
        formData.append('hash', hash);

        const init: any = {
            body: formData,
            credentials: 'include',
            method: 'post',
        };

        fetch(url, init)
            .then(r => {
                if (r.ok) {
                    dispatch({ type: 'UPLOAD_BALLOT_MANIFEST_OK' });
                } else {
                    dispatch({ type: 'UPLOAD_BALLOT_MANIFEST_FAIL' });
                }
            })
            .catch(() => {
                dispatch({ type: 'UPLOAD_BALLOT_MANIFEST_NETWORK_FAIL' });
            });
    };
};


export default uploadBallotManifest;
