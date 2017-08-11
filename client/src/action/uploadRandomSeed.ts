import { Dispatch } from 'redux';

import { apiHost } from '../config';


const uploadRandomSeed = (seed: string) => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: 'UPLOAD_RANDOM_SEED_SEND' });

        const url = `http://${apiHost}/upload-random-seed`;
        const body = { seed };

        fetch(url, { method: 'post', body })
            .then(r => {
                if (r.ok) {
                    dispatch({ type: 'UPLOAD_RANDOM_SEED_RECEIVE' });
                } else {
                    dispatch({ type: 'UPLOAD_RANDOM_SEED_FAIL' });
                }
            })
            .catch(() => {
                dispatch({ type: 'UPLOAD_RANDOM_SEED_NETWORK_FAIL' });
            });
    };
};


export default uploadRandomSeed;
