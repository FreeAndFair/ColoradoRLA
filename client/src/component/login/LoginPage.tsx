import * as React from 'react';

import LoginFormContainer from './LoginFormContainer';


export default class LoginPage extends React.Component<any, any> {
    public render() {
        return (
            <div className='pt-card login-page'>
                <h2>CORLA User Login</h2>
                <LoginFormContainer />
            </div>
        );
    }
}
