import * as React from 'react';
import { connect } from 'react-redux';

import { Redirect } from 'react-router-dom';

import session from 'corla/session';

import LoginPage from './Page';


export class LoginContainer extends React.Component<any, any> {
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
