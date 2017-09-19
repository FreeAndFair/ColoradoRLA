import * as React from 'react';

import withSync from 'corla/component/withSync';

import ContestDetailPage from './DetailPage';


class ContestDetailContainer extends React.Component<any, any> {
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

const select = (state: any) => {
    const { sos } = state;
    const { contests } = sos;

    return { contests };
};


export default withSync(
    ContestDetailContainer,
    'DOS_CONTEST_DETAIL_SYNC',
    select,
);
