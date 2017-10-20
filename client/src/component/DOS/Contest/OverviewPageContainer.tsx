import * as React from 'react';

import withSync from 'corla/component/withSync';

import OverviewPage from './OverviewPage';


function select(dosState: DOS.AppState) {
    return {
        contests: dosState.contests,
        dosState,
    };
}


export default withSync(
    OverviewPage,
    'DOS_CONTEST_OVERVIEW_SYNC',
    select,
);
