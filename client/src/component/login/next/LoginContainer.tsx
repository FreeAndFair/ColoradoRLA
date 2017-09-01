import * as React from 'react';
import { connect } from 'react-redux';

import { Redirect } from 'react-router-dom';

import LoginPage from './LoginPage';


export class LoginContainer extends React.Component<any, any> {
    public render() {
        const { location, loggedIn } = this.props;

        if (loggedIn) {
            const to = location.state
                     ? location.state.from
                     : '/';

            return <Redirect to={ to } />;
        }

        return <LoginPage />;
    }
}

const mapStateToProps = ({ loggedIn }: any) => ({ loggedIn });


export default connect(mapStateToProps)(LoginContainer);
