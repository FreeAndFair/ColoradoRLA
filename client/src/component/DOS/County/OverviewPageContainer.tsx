import * as React from 'react';

import * as _ from 'lodash';

import withSync from 'corla/component/withSync';

import CountyOverviewPage from './OverviewPage';


function select(state: AppState) {
    const { sos } = state;
    const { countyStatus } = sos!;

    return { countyStatus, sos };
}


export default withSync(
    CountyOverviewPage,
    'DOS_COUNTY_OVERVIEW_SYNC',
    select,
);
