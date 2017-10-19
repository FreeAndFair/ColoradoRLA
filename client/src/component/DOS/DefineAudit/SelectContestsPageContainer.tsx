import * as React from 'react';
import { Redirect } from 'react-router-dom';

import { History } from 'history';

import withPoll from 'corla/component/withPoll';

import SelectContestsPage from './SelectContestsPage';

import selectContestsForAudit from 'corla/action/dos/selectContestsForAudit';


interface ContainerProps {
    auditedContests: DOS.AuditedContests;
    contests: DOS.Contests;
    history: History;
    isAuditable: OnClick;
    sos: DOS.AppState;
}

class SelectContestsPageContainer extends React.Component<ContainerProps> {
    public render() {
        const {
            auditedContests,
            contests,
            history,
            isAuditable,
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
            isAuditable,
            nextPage: () => history.push('/sos/audit/seed'),
            selectContestsForAudit,
        };

        return <SelectContestsPage { ...props } />;
    }
}

function select(state: AppState) {
    const { sos } = state;

    if (!sos) { return {}; }

    const isAuditable = (contestId: number): boolean => {
        if (!sos.auditTypes) { return false; }

        const t = sos.auditTypes[contestId];

        return t !== 'HAND_COUNT' && t !== 'NOT_AUDITABLE';
    };

    return {
        auditedContests: sos.auditedContests,
        contests: sos.contests,
        isAuditable,
        sos,
    };
}


export default withPoll(
    SelectContestsPageContainer,
    'DOS_SELECT_CONTESTS_POLL_START',
    'DOS_SELECT_CONTESTS_POLL_STOP',
    select,
);
