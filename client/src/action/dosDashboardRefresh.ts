import { Dispatch } from 'redux';

import { apiHost } from '../config';

import createFetchAction from './createFetchAction';


const url = `http://${apiHost}/dos-dashboard`;

const dosDashboardRefresh = createFetchAction({
    failType: 'DOS_DASHBOARD_REFRESH_FAIL',
    networkFailType: 'DOS_DASHBOARD_REFRESH_NETWORK_FAIL',
    okType: 'DOS_DASHBOARD_REFRESH_RECEIVE',
    sendType: 'DOS_DASHBOARD_REFRESH_SEND',
    url,
});


export default dosDashboardRefresh;
