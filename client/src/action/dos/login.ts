import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('auth-state-admin');

const dosLogin = createSubmitAction({
    failType: 'DOS_LOGIN_FAIL',
    networkFailType: 'DOS_LOGIN_NETWORK_FAIL',
    okType: 'DOS_LOGIN_OK',
    sendType: 'DOS_LOGIN_SEND',
    url,
});


export default (username: string, password: string) =>
    dosLogin({ username, password });
