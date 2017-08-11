import { Dispatch } from 'redux';

import { apiHost } from '../config';


const authStateAdmin = (username: string, password: string) => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: 'AUTH_STATE_ADMIN_SEND' });

        const url = `http://${apiHost}/auth-state-admin`;
        const body = { username, password };

        fetch(url, { method: 'post', body })
            .then(r => {
                if (r.ok) {
                    dispatch({ type: 'AUTH_STATE_ADMIN_RECEIVE' });
                } else {
                    dispatch({ type: 'AUTH_STATE_ADMIN_FAIL' });
                }
            })
            .catch(() => {
                dispatch({ type: 'AUTH_STATE_ADMIN_NETWORK_FAIL' });
            });
    };
};


export default authStateAdmin;
