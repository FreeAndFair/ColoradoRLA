import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import LoginForm, { FormFields } from './LoginForm';

import authCountyAdmin from '../../action/authCountyAdmin';
import authStateAdmin from '../../action/authStateAdmin';


export class LoginFormContainer extends React.Component<any, any> {
    public render() {
        const { submit } = this.props;

        return <LoginForm submit={ submit } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => {
    const b = bindActionCreators({
        authCountyAdmin,
        authStateAdmin,
    }, dispatch);

    const submit = ({dashboard, username, password}: any) => {
        switch (dashboard) {
            case 'sos': return b.authStateAdmin(username, password);
            case 'county': return b.authCountyAdmin(username, password);
            default: return null;
        }
    };

    return { submit };
};

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(LoginFormContainer);
