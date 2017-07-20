import * as React from 'react';
import { connect } from 'react-redux';

import LoginPage from '../component/LoginPage';


class Login extends React.Component<any, any> {
    public render() {
        return <LoginPage />;
    }
}

const mapStateToProps = (state: any) => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(Login);
