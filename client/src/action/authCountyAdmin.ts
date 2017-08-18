import { Dispatch } from 'redux';

import { endpoint } from '../config';

import createSubmitAction from './createSubmitAction';


const url = endpoint('auth-county-admin');

const authCountyAdmin = createSubmitAction({
    failType: 'AUTH_COUNTY_ADMIN_FAIL',
    networkFailType: 'AUTH_COUNTY_ADMIN_NETWORK_FAIL',
    okType: 'AUTH_COUNTY_ADMIN_OK',
    sendType: 'AUTH_COUNTY_ADMIN_SEND',
    url,
});


export default (username: string, password: string) =>
    authCountyAdmin({ username, password });
