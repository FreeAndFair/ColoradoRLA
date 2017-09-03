import * as React from 'react';
import { connect } from 'react-redux';

import ContestDetailPage from './DetailPage';

import dosFetchContests from 'corla/action/dosFetchContests';


class ContestDetailContainer extends React.Component<any, any> {
    public render() {
        const { contests } = this.props;

        if (!contests) {
            dosFetchContests();
            return <div />;
        }

        const { contestId } = this.props.match.params;
        const contest = this.props.contests[contestId];

        if (!contest) {
            // This might only ever be evidence of a bug.
            dosFetchContests();
            return <div />;
        }

        return <ContestDetailPage contest={ contest } />;
    }
}

const mapStateToProps = ({ sos }: any) => ({ contests: sos.contests });


export default connect(mapStateToProps)(ContestDetailContainer);
