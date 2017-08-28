import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import CountyAuditPage from './CountyAuditPage';

import notice from '../../../notice';

import canAudit from '../../../selector/county/canAudit';


class CountyAuditContainer extends React.Component<any, any> {
    public render() {
        if (!this.props.canAudit) {
            notice.danger('Not ready to begin audit.');

            return <Redirect to={ '/county' } />;
        }

        return <CountyAuditPage />;
    }
}

const mapStateToProps = (state: any) => {
    return {
        canAudit: canAudit(state),
    };
};


export default connect(mapStateToProps)(CountyAuditContainer);
