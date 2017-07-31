import * as React from 'react';
import { connect } from 'react-redux';

import { Redirect } from 'react-router-dom';

import LoginPage from './LoginPage';


interface LoginContainerProps {
    loggedIn: boolean;
}

export class LoginContainer extends React.Component<LoginContainerProps & any, any> {
    public render() {
        const { loggedIn } = this.props;

        if (loggedIn) {
            const { from } = this.props.location.state;
            return <Redirect to={ from } />;
        }

        return <LoginPage />;
    }
}

const mapStateToProps = ({ loggedIn }: any) => ({ loggedIn });

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(LoginContainer);
