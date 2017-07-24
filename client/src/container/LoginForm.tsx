import * as React from 'react';
import { connect } from 'react-redux';

import LoginForm, { FormFields } from '../component/LoginForm';


export class LoginFormContainer extends React.Component<any, any> {
    public render() {
        const submit = this.props;

        return <LoginForm submit={ submit } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({
    submit: (_: FormFields) => { return; },
});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(LoginFormContainer);
