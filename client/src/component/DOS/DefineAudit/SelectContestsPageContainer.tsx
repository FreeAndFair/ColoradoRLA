import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

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

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            auditedContests,
            back: () => history.push('/sos/audit'),
            contests,
            nextPage: () => history.push('/sos/audit/seed'),
            selectContestsForAudit,
        };

        return <SelectContestsPage { ...props } />;
    }
}


const mapStateToProps = ({ sos }: any) => ({
    auditedContests: sos.auditedContests,
    contests: sos.contests,
    sos,
});

export default connect(mapStateToProps)(SelectContestsPageContainer);
