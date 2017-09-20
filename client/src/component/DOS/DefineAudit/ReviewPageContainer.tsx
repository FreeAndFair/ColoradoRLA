import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import withSync from 'corla/component/withSync';

import ReviewPage from './ReviewPage';

import publishBallotsToAudit from 'corla/action/dos/publishBallotsToAudit';


class ReviewPageContainer extends React.Component<any, any> {
    public render() {
        const { history, sos } = this.props;

        if (!sos) {
            return <div />;
        }

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            back: () => history.push('/sos/audit/seed'),
            publishBallotsToAudit,
            saveAndDone: () => history.push('/sos'),
            sos,
        };

        return <ReviewPage { ...props } />;
    }
}

const select = (state: any) => {
    const { sos } = state;

    return { sos };
};


export default withSync(
    ReviewPageContainer,
    'DOS_DEFINE_AUDIT_REVIEW_SYNC',
    select,
);
