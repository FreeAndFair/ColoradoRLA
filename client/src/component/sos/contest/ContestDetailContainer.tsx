import * as React from 'react';
import { connect } from 'react-redux';

import ContestDetailPage from './ContestDetailPage';


class ContestDetailContainer extends React.Component<any, any> {
    public render() {
        const { contestId } = this.props.match.params;
        const contest = this.props.contests[contestId];

        return <ContestDetailPage contest={ contest } />;
    }
}

const mapStateToProps = ({ contests }: any) => ({ contests });

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(ContestDetailContainer);
