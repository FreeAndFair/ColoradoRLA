import { Dispatch } from 'redux';

import { apiHost } from '../config';


const dosDashboardRefresh = () => {
    return (dispatch: Dispatch<any>) => {
        dispatch({ type: 'DOS_DASHBOARD_REFRESH_SEND' });

        const url = `http://${apiHost}/dos-dashboard`;

        fetch(url)
            .then(r => {
                if (!r.ok) {
                    dispatch({ type: 'DOS_DASHBOARD_REFRESH_FAIL' });
                    return;
                }

                r.json().then((data: any) => {
                    dispatch({ type: 'DOS_DASHBOARD_REFRESH_RECEIVE', data });
                });
            })
            .catch(() => {
                dispatch({ type: 'DOS_DASHBOARD_REFRESH_NETWORK_FAIL' });
            });
    };
};


export default dosDashboardRefresh;
