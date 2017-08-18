import { Dispatch } from 'redux';

import { endpoint } from '../config';

import createSubmitAction from './createSubmitAction';


const url = endpoint('auth-state-admin');

const authStateAdmin = createSubmitAction({
    failType: 'AUTH_STATE_ADMIN_FAIL',
    networkFailType: 'AUTH_STATE_ADMIN_NETWORK_FAIL',
    okType: 'AUTH_STATE_ADMIN_OK',
    sendType: 'AUTH_STATE_ADMIN_SEND',
    url,
});


export default (username: string, password: string) =>
    authStateAdmin({ username, password });
