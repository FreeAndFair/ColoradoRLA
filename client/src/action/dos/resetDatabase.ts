import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('reset-database');

const resetDatabase = createSubmitAction({
    failType: 'RESET_DATABASE_FAIL',
    networkFailType: 'RESET_DATABASE_NETWORK_FAIL',
    okType: 'RESET_DATABASE_OK',
    sendType: 'RESET_DATABASE_SEND',
    url,
});


export default () => resetDatabase({});
