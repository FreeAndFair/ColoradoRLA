import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import CountyAuditPage from './CountyAuditPage';

import notice from '../../../notice';


class CountyAuditContainer extends React.Component<any, any> {
    public render() {
        if (!this.props.auditInProgress) {
            notice.danger('Not ready to begin audit.');
            return <Redirect to={ '/county' } />;
        }

        return <CountyAuditPage />;
    }
}

const mapStateToProps = (state: any) => {
    return {
        auditInProgress: state.county.asm.auditBoard.currentState === 'AUDIT_IN_PROGRESS',
    };
};


export default connect(mapStateToProps)(CountyAuditContainer);
