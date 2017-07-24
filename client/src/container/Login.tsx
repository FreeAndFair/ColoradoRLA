import * as React from 'react';
import { connect } from 'react-redux';

import { Redirect } from 'react-router-dom';

import LoginPage from '../component/LoginPage';


interface LoginContainerProps {
    loggedIn: boolean;
}

export class LoginContainer extends React.Component<LoginContainerProps, any> {
    public render() {
        const { loggedIn } = this.props;

        if (loggedIn) {
            return <Redirect to='/' />;
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
