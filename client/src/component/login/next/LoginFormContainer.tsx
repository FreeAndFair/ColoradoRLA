import * as React from 'react';
import { connect } from 'react-redux';

import PasswordForm from './PasswordForm';
import SecondFactorForm from './SecondFactorForm';


export class LoginFormContainer extends React.Component<any, any> {
    public render() {
        const { loginChallenge } = this.props;

        if (loginChallenge) {
            return <SecondFactorForm { ...this.props } />;
        } else {
            return <PasswordForm { ...this.props } />;
        }
    }
}

const mapStateToProps = (state: any) => {
    const { loggedIn, loginChallenge } = state;

    return { loggedIn, loginChallenge };
};


export default connect(mapStateToProps)(LoginFormContainer);
