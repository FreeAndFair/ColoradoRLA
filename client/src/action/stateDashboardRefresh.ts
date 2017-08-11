import { Dispatch } from 'redux';

import { apiHost } from '../config';


const stateDashboardRefresh = () => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: 'STATE_DASHBOARD_REFRESH_SEND' });

        const url = `http://${apiHost}/state-refresh`;

        fetch(url)
            .then(r => {
                if (!r.ok) {
                    dispatch({ type: 'STATE_DASHBOARD_REFRESH_FAIL' });
                    return;
                }

                r.json().then((data: any) => {
                    dispatch({ type: 'STATE_DASHBOARD_REFRESH_RECEIVE', data });
                });
            })
            .catch(() => {
                dispatch({ type: 'STATE_DASHBOARD_REFRESH_NETWORK_FAIL' });
            });
    };
};


export default stateDashboardRefresh;
