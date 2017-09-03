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

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(ContestOverviewContainer);
