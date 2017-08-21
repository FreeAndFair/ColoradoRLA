export const debug = true;  // Inlined by Webpack

const scheme = 'http';
const hostname = 'localhost';
const port = 8888;

const devEndpointPrefix = `${scheme}://${hostname}:${port}`;
const prodEndpointPrefix = '/api';

const endpointPrefix = DEBUG ? devEndpointPrefix : prodEndpointPrefix;

export const endpoint = (path: string) => `${endpointPrefix}/${path}`;
