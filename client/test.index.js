const importAll = r => r.keys().forEach(r);

const unitTestCtx = require.context('./src', true, /\.test$/);
const integrationTestCtx = require.context('./test', true, /\.test$/);

importAll(unitTestCtx);
importAll(integrationTestCtx);
