import * as React from 'react';

import withSync from 'corla/component/withSync';

import counties from 'corla/data/counties';

import CountyDetailPage from './DetailPage';


class CountyDetailContainer extends React.Component<any, any> {
    public render() {
        const { countyStatus } = this.props;

        const { countyId } = this.props.match.params;
        const county: any = counties[countyId];

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

const select = (state: any) => {
    const { sos } = state;
    const { countyStatus } = sos;

    return { countyStatus };
};


export default withSync(
    CountyDetailContainer,
    'DOS_COUNTY_DETAIL_SYNC',
    select,
);
