import * as React from 'react';
import { connect } from 'react-redux';

import counties from 'corla/data/counties';

import CountyDetailPage from './DetailPage';

import dosDashboardRefresh from 'corla/action/dos/dashboardRefresh';


class CountyDetailContainer extends React.Component<any, any> {
    public render() {
        const { countyStatus } = this.props;

        const { countyId } = this.props.match.params;
        const county: any = counties[countyId];

        if (!county) {
            dosDashboardRefresh();
            return <div />;
        }

        const status = countyStatus[countyId];

        return <CountyDetailPage county={ county } status={ status } />;
    }
}

const mapStateToProps = ({ sos }: any) => ({ countyStatus: sos.countyStatus });


export default connect(mapStateToProps)(CountyDetailContainer);
