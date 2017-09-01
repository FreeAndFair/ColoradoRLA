import * as React from 'react';
import { connect } from 'react-redux';

import LoginForm from './LoginForm';


export class LoginFormContainer extends React.Component<any, any> {
    public render() {
        const { loggedIn } = this.props;

        return <LoginForm />;
    }
}

const mapStateToProps = (state: any) => {
    const { loggedIn } = state;

    return { loggedIn };
};


export default connect(mapStateToProps)(LoginFormContainer);
