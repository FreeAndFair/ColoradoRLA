import * as React from 'react';
import { connect } from 'react-redux';

import ContestOverviewPage from './OverviewPage';


class ContestOverviewContainer extends React.Component<any, any> {
    public render() {
        return <ContestOverviewPage { ...this.props } />;
    }
}

const mapStateToProps = ({ sos }: any) => ({
    contests: sos.contests,
    sos,
});


export default connect(mapStateToProps)(ContestOverviewContainer);
