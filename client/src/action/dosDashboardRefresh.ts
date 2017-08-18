import { Dispatch } from 'redux';

import { endpoint } from '../config';

import createFetchAction from './createFetchAction';


const url = endpoint('dos-dashboard');

const dosDashboardRefresh = createFetchAction({
    failType: 'DOS_DASHBOARD_REFRESH_FAIL',
    networkFailType: 'DOS_DASHBOARD_REFRESH_NETWORK_FAIL',
    okType: 'DOS_DASHBOARD_REFRESH_OK',
    sendType: 'DOS_DASHBOARD_REFRESH_SEND',
    url,
});


export default dosDashboardRefresh;
