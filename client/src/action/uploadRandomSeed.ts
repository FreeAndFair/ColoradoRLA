import { Dispatch } from 'redux';

import { endpoint } from '../config';

import createSubmitAction from './createSubmitAction';


const url = endpoint('random-seed');

const uploadRandomSeed = createSubmitAction({
    failType: 'UPLOAD_RANDOM_SEED_FAIL',
    networkFailType: 'UPLOAD_RANDOM_SEED_NETWORK_FAIL',
    okType: 'UPLOAD_RANDOM_SEED_OK',
    sendType: 'UPLOAD_RANDOM_SEED_SEND',
    url,
});


export default (seed: string) => uploadRandomSeed({ seed });
