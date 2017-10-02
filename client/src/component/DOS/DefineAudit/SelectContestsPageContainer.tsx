import * as React from 'react';
import { Redirect } from 'react-router-dom';

import withPoll from 'corla/component/withPoll';

import SelectContestsPage from './SelectContestsPage';

import selectContestsForAudit from 'corla/action/dos/selectContestsForAudit';


class SelectContestsPageContainer extends React.Component<any, any> {
    public render() {
        const {
            auditedContests,
            contests,
            history,
            sos,
        } = this.props;

        if (!sos) {
            return <div />;
        }

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            auditedContests,
            back: () => history.push('/sos/audit'),
            contests,
            nextPage: () => history.push('/sos/audit/seed'),
            selectContestsForAudit,
            sos,
        };

        return <SelectContestsPage { ...props } />;
    }
}

const select = (state: any) => {
    const { sos } = state;

    if (!sos) { return {}; }

    return {
        auditedContests: sos.auditedContests,
        contests: sos.contests,
        sos,
    };
};


export default withPoll(
    SelectContestsPageContainer,
    'DOS_SELECT_CONTESTS_POLL_START',
    'DOS_SELECT_CONTESTS_POLL_STOP',
    select,
);
