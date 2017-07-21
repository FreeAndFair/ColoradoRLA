import * as React from 'react';
import { connect } from 'react-redux';

import LoginForm from '../component/LoginForm';


export class LoginFormContainer extends React.Component<any, any> {
    public render() {
        return <LoginForm />;
    }
}

const mapStateToProps = (state: any) => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(LoginFormContainer);
