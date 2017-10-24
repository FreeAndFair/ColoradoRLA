import * as React from 'react';
import { match } from 'react-router-dom';

import withDOSState from 'corla/component/withDOSState';
import withSync from 'corla/component/withSync';

import ContestDetailPage from './DetailPage';


interface ContainerProps {
    contests: DOS.Contests;
    match: match<any>;
}

class ContestDetailContainer extends React.Component<ContainerProps> {
    public render() {
        const { contests } = this.props;

        if (!contests) {
            return <div />;
        }

        const { contestId } = this.props.match.params;
        const contest = this.props.contests[contestId];

        if (!contest) {
            return <div />;
        }

        return <ContestDetailPage contest={ contest } />;
    }
}

function select(dosState: DOS.AppState) {
    const { contests } = dosState;

    return { contests };
}


export default withSync(
    withDOSState(ContestDetailContainer),
    'DOS_CONTEST_DETAIL_SYNC',
    select,
);
