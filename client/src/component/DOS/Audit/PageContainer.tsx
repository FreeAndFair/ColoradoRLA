import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import AuditPage from './Page';


class AuditPageContainer extends React.Component<any, any> {
    public state: any = {
        formValid: false,
    };

    public render() {
        const { election, history, riskLimit, sos } = this.props;

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            election,
            formValid: this.state.formValid,
            nextPage: () => history.push('/sos/audit/select-contests'),
            riskLimit,
            setFormValid: this.setFormValid,
        };

        return <AuditPage { ...props } />;
    }

    private setFormValid = (formValid: boolean) => {
        this.setState({ formValid });
    }
}


const mapStateToProps = ({ sos }: any) => {
    const { election, riskLimit } = sos;

    return { election, riskLimit, sos };
};

export default connect(mapStateToProps)(AuditPageContainer);
