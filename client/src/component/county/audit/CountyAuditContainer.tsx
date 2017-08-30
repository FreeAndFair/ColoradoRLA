import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import CountyAuditPage from './CountyAuditPage';
import EndOfRoundPageContainer from './EndOfRoundPageContainer';

import notice from '../../../notice';

import auditComplete from '../../../selector/county/auditComplete';
import canAudit from '../../../selector/county/canAudit';
import roundInProgress from '../../../selector/county/roundInProgress';


class CountyAuditContainer extends React.Component<any, any> {
    public render() {
        if (!this.props.canAudit) {
            notice.danger('Not ready to begin audit.');

            return <Redirect to={ '/county' } />;
        }

        if (this.props.showEndOfRoundPage) {
            return <EndOfRoundPageContainer />;
        }

        return <CountyAuditPage />;
    }
}

const mapStateToProps = (state: any) => {
    const showEndOfRoundPage = !roundInProgress(state) && !auditComplete(state);

    return {
        canAudit: canAudit(state),
        showEndOfRoundPage,
    };
};


export default connect(mapStateToProps)(CountyAuditContainer);
