import { endpoint } from 'corla/config';

import action from '.';

import createSubmitAction from './createSubmitAction';


const url = endpoint('/unauthenticate');

const submit = createSubmitAction({
    failType: 'EXPIRE_SESSION_FAIL',
    networkFailType: 'EXPIRE_SESSION_NETWORK_FAIL',
    okType: 'EXPIRE_SESSION_OK',
    sendType: 'EXPIRE_SESSION_SEND',
    url,
});


export default () => submit({});
