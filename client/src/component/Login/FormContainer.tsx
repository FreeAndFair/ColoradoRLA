import * as React from 'react';
import { connect } from 'react-redux';

import PasswordForm from './PasswordForm';
import SecondFactorForm from './SecondFactorForm';


interface Props {
    dashboard: Dashboard;
    loginChallenge: LoginChallenge;
    username: string;
}

export class LoginFormContainer extends React.Component<Props> {
    public render() {
        const { loginChallenge } = this.props;

        if (loginChallenge) {
            return <SecondFactorForm { ...this.props } />;
        } else {
            return <PasswordForm />;
        }
    }
}

function select(state: LoginAppState) {
    const { dashboard, loginChallenge, username } = state;

    return { dashboard, loginChallenge, username };
}


export default connect(select)(LoginFormContainer);
