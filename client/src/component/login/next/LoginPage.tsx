import * as React from 'react';

import LoginFormContainer from './LoginFormContainer';


const LoginPage = () => {
    return (
        <div className='pt-card login-page'>
            <h2>CORLA User Login</h2>
            <LoginFormContainer />
        </div>
    );
};


export default LoginPage;
