import { endpoint } from '../config';

import createSubmitAction from './createSubmitAction';


const url = endpoint('reset-database');

const resetDatabase = createSubmitAction({
    failType: 'RESET_DATABASE_FAIL',
    networkFailType: 'RESET_DATABASE_NETWORK_FAIL',
    okType: 'RESET_DATABASE_OK',
    sendType: 'RESET_DATABASE_SEND',
    url,
});


export default () => resetDatabase({});
