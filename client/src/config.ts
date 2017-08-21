export const debug = DEBUG;  // Inlined by Webpack

const scheme = 'http';
const hostname = 'localhost';
const port = 8888;

const devEndpointPrefix = `${scheme}://${hostname}:${port}`;
const prodEndpointPrefix = '/api';

const endpointPrefix = debug ? devEndpointPrefix : prodEndpointPrefix;

export const endpoint = (path: string) => `${endpointPrefix}/${path}`;
