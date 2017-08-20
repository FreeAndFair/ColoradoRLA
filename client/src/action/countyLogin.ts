import { endpoint } from '../config';

import createSubmitAction from './next/createSubmitAction';


const url = endpoint('auth-county-admin');

const countyLogin = createSubmitAction({
    failType: 'COUNTY_LOGIN_FAIL',
    networkFailType: 'COUNTY_LOGIN_NETWORK_FAIL',
    okType: 'COUNTY_LOGIN_OK',
    sendType: 'COUNTY_LOGIN_SEND',
    url,
});


export default (username: string, password: string) =>
    countyLogin({ username, password });
