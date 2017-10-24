import * as React from 'react';

import withDOSState from 'corla/component/withDOSState';
import withSync from 'corla/component/withSync';

import OverviewPage from './OverviewPage';


function select(dosState: DOS.AppState) {
    return {
        contests: dosState.contests,
        dosState,
    };
}


export default withSync(
    withDOSState(OverviewPage),
    'DOS_CONTEST_OVERVIEW_SYNC',
    select,
);
