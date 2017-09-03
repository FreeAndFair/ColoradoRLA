import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('auth-admin');

const login2F = createSubmitAction({
    failType: 'LOGIN_2F_FAIL',
    networkFailType: 'LOGIN_2F_NETWORK_FAIL',
    okType: 'LOGIN_2F_OK',
    sendType: 'LOGIN_2F_SEND',
    url,
});


export default (username: string, secondFactor: string) =>
    login2F({ username, second_factor: secondFactor });
