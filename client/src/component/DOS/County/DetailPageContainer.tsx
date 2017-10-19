import * as React from 'react';
import { match } from 'react-router-dom';

import withSync from 'corla/component/withSync';

import counties from 'corla/data/counties';

import CountyDetailPage from './DetailPage';


interface ContainerProps {
    countyStatus: DosCountyStatuses;
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

function select(state: AppState) {
    const { sos } = state;
    const { countyStatus } = sos!;

    return { countyStatus };
}


export default withSync(
    CountyDetailContainer,
    'DOS_COUNTY_DETAIL_SYNC',
    select,
);
