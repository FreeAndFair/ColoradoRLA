import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('random-seed');

const uploadRandomSeed = createSubmitAction({
    failType: 'UPLOAD_RANDOM_SEED_FAIL',
    networkFailType: 'UPLOAD_RANDOM_SEED_NETWORK_FAIL',
    okType: 'UPLOAD_RANDOM_SEED_OK',
    sendType: 'UPLOAD_RANDOM_SEED_SEND',
    url,
});


export default (seed: string) => uploadRandomSeed({ seed });
