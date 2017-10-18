import * as React from 'react';

import withSync from 'corla/component/withSync';

import OverviewPage from './OverviewPage';


function select(state: AppState) {
    const { sos } = state;

    if (!sos) {
        return {};
    }

    return {
        contests: sos.contests,
        sos,
    };
}


export default withSync(
    OverviewPage,
    'DOS_CONTEST_OVERVIEW_SYNC',
    select,
);
