import { endpoint } from 'corla/config';

import action from '.';

import createSubmitAction from './createSubmitAction';


const url = endpoint('/unauthenticate');

const submit = createSubmitAction({
    failType: 'LOGOUT_FAIL',
    networkFailType: 'LOGOUT_NETWORK_FAIL',
    okType: 'LOGOUT_OK',
    sendType: 'LOGOUT_SEND',
    url,
});


export default () => submit({});
