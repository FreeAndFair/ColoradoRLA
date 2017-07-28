import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import LoginForm, { FormFields } from './LoginForm';

import submitLogin from '../../action/submitLogin';


export class LoginFormContainer extends React.Component<any, any> {
    public render() {
        const { submit } = this.props;

        return <LoginForm submit={ submit } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => bindActionCreators({
    submit: submitLogin,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(LoginFormContainer);
