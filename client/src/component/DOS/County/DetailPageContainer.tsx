import * as React from 'react';
import { match } from 'react-router-dom';

import withDOSState from 'corla/component/withDOSState';
import withSync from 'corla/component/withSync';

import counties from 'corla/data/counties';

import CountyDetailPage from './DetailPage';


interface ContainerProps {
    countyStatus: DOS.CountyStatuses;
    match: match<any>;
}

class CountyDetailContainer extends React.Component<ContainerProps> {
    public render() {
        const { countyStatus } = this.props;

        const { countyId } = this.props.match.params;
        const county = counties[countyId];

        if (!county) {
            return <div />;
        }

        const status = countyStatus[countyId];

        if (!status) {
            return <div />;
        }

        return <CountyDetailPage county={ county } status={ status } />;
    }
}

function select(dosState: DOS.AppState) {
    const { countyStatus } = dosState;

    return { countyStatus };
}


export default withSync(
    withDOSState(CountyDetailContainer),
    'DOS_COUNTY_DETAIL_SYNC',
    select,
);
