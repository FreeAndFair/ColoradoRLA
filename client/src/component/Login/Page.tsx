import * as React from 'react';

import LicenseFooter from 'corla/component/LicenseFooter';
import LoginFormContainer from './FormContainer';


const LoginPage = () => {
    return (
        <div>
            <div className='pt-card login-page'>
                <h2>CORLA User Login</h2>
                <LoginFormContainer />
            </div>
            <LicenseFooter />
        </div>
    );
};


export default LoginPage;
