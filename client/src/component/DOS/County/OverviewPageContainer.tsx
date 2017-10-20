import * as React from 'react';

import * as _ from 'lodash';

import withSync from 'corla/component/withSync';

import CountyOverviewPage from './OverviewPage';


function select(dosState: DOS.AppState) {
    const { countyStatus } = dosState;

    return { countyStatus, dosState };
}


export default withSync(
    CountyOverviewPage,
    'DOS_COUNTY_OVERVIEW_SYNC',
    select,
);
