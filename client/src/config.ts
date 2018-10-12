export const debug = DEBUG;  // Inlined by Webpack

const scheme = 'http';
const hostname = 'localhost';
const port = 8888;

const devEndpointPrefix = `${scheme}://${hostname}:${port}`;
const prodEndpointPrefix = '/api';

const endpointPrefix = DEBUG ? devEndpointPrefix : prodEndpointPrefix;

export const endpoint = (path: string) => `${endpointPrefix}/${path}`;

export const timezone = 'America/Denver';

export const helpEmail = 'voting.systems@sos.state.co.us';

export const helpTel = '877-436-5677';

export const pollDelay
    = debug
    ? 1000 * 5
    : 1000 * 30;


// Notification timeouts are in milliseconds.
export const defaultOkTimeout = 10000;

// No timeout, require manual dismissal.
export const defaultDangerTimeout = 0;
export const defaultWarningTimeout = 0;
