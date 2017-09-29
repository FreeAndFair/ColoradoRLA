export const debug = DEBUG;  // Inlined by Webpack

const scheme = 'http';
const hostname = 'localhost';
const port = 8888;

const devEndpointPrefix = `${scheme}://${hostname}:${port}`;
const prodEndpointPrefix = '/api';

const endpointPrefix = DEBUG ? devEndpointPrefix : prodEndpointPrefix;

export const endpoint = (path: string) => `${endpointPrefix}/${path}`;

export const timezone = 'America/Denver';

export const defaultElectionDate = '2017-11-07';

export const defaultPublicMeetingDate = '2017-11-17';

export const helpEmail = 'help@example.com';

export const helpTel = '555-555-5555';

export const pollDelay
    = debug
    ? 1000 * 5
    : 1000 * 30;

export const defaultNoticeTimeout = 0;
