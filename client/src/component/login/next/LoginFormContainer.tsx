import * as React from 'react';
import { connect } from 'react-redux';

import LoginForm from './LoginForm';


export class LoginFormContainer extends React.Component<any, any> {
    public render() {
        return <LoginForm { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { loggedIn, loginChallenge } = state;

    return { loggedIn, loginChallenge };
};


export default connect(mapStateToProps)(LoginFormContainer);
