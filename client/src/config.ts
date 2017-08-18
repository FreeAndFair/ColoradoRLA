const scheme = 'http';
const hostname = 'localhost';
const port = 8888;

const devEndpointPrefix = `${scheme}://${hostname}:${port}`;
const prodEndpointPrefix = '/api';

export const endpoint = (path: string) => `${devEndpointPrefix}/${path}`;
