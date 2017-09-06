export const debug = true;  // Inlined by Webpack

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
