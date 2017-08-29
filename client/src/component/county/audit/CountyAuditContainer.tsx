import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import CountyAuditPage from './CountyAuditPage';
import EndOfRoundPage from './EndOfRoundPage';

import notice from '../../../notice';

import canAudit from '../../../selector/county/canAudit';
import showEndOfRoundPage from '../../../selector/county/showEndOfRoundPage';


class CountyAuditContainer extends React.Component<any, any> {
    public render() {
        if (!this.props.canAudit) {
            notice.danger('Not ready to begin audit.');

            return <Redirect to={ '/county' } />;
        }

        if (this.props.showEndOfRoundPage) {
            return <EndOfRoundPage />;
        }

        return <CountyAuditPage />;
    }
}

const mapStateToProps = (state: any) => {
    return {
        canAudit: canAudit(state),
        showEndOfRoundPage: showEndOfRoundPage(state),
    };
};


export default connect(mapStateToProps)(CountyAuditContainer);
