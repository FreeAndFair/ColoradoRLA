import { Dispatch } from 'redux';

import { apiHost } from '../config';

import createFetchAction from './createFetchAction';


const url = `http://${apiHost}/county-dashboard`;

const countyDashboardRefresh = createFetchAction({
    failType: 'COUNTY_DASHBOARD_REFRESH_FAIL',
    networkFailType: 'COUNTY_DASHBOARD_REFRESH_NETWORK_FAIL',
    okType: 'COUNTY_DASHBOARD_REFRESH_OK',
    sendType: 'COUNTY_DASHBOARD_REFRESH_SEND',
    url,
});


export default countyDashboardRefresh;
