import * as React from 'react';

import withSync from 'corla/component/withSync';

import OverviewPage from './OverviewPage';


const select = (state: any) => {
    const { sos } = state;

    return {
        contests: sos.contests,
        sos,
    };
};


export default withSync(
    OverviewPage,
    'DOS_CONTEST_OVERVIEW_SYNC',
    select,
);
