import action from '.';

import expireSession from './expireSession';


export default () => {
    expireSession();
    action('LOGOUT');
};
