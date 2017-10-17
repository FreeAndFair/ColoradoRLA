import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';
import { Redirect } from 'react-router-dom';

import session from 'corla/session';

import LoginPage from './Page';


type LoginProps = RouteComponentProps<void>;

export class LoginContainer extends React.Component<LoginProps> {
    public render() {
        const { location } = this.props;

        if (session.active()) {
            const to = location.state
                     ? location.state.from
                     : '/';

            return <Redirect to={ to } />;
        }

        return <LoginPage />;
    }
}


export default LoginContainer;
