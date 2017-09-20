import action from '.';

import expireSession from './expireSession';


export default async () => {
    await expireSession();
    action('LOGOUT');
};
