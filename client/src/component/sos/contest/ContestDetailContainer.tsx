import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import fetchContests from '../../../action/fetchContests';

import ContestDetailPage from './ContestDetailPage';


class ContestDetailContainer extends React.Component<any, any> {
    public render() {
        const { contests, fetchContests } = this.props;

        if (!contests) {
            fetchContests();
            return <div />;
        }

        const { contestId } = this.props.match.params;
        const contest = this.props.contests[contestId];

        if (!contest) {
            // This might only ever be evidence of a bug.
            fetchContests();
            return <div />;
        }

        return <ContestDetailPage contest={ contest } />;
    }
}

const mapStateToProps = ({ sos }: any) => ({ contests: sos.contests });

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    fetchContests,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(ContestDetailContainer);
