import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import AuditPage from './Page';

import setRiskLimit from 'corla/action/dos/setRiskLimit';


class AuditPageContainer extends React.Component<any, any> {
    public render() {
        const { election, history, riskLimit, sos } = this.props;

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            election,
            nextPage: () => history.push('/sos/audit/select-contests'),
            riskLimit,
            setRiskLimit,
        };

        return <AuditPage { ...props } />;
    }
}


const mapStateToProps = ({ sos }: any) => {
    const { election, riskLimit } = sos;

    return { election, riskLimit, sos };
};

export default connect(mapStateToProps)(AuditPageContainer);
