import * as React from 'react';

import * as _ from 'lodash';

import withSync from 'corla/component/withSync';

import counties from 'corla/data/counties';

import CountyOverviewPage from './OverviewPage';


const select = (state: any) => {
    const { sos } = state;
    const { countyStatus } = sos;

    return { counties, countyStatus, sos };
};


export default withSync(
    CountyOverviewPage,
    'DOS_COUNTY_OVERVIEW_SYNC',
    select,
);
