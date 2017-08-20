import * as React from 'react';
import { connect } from 'react-redux';

import LoginForm, { FormFields } from './LoginForm';

import countyLogin from '../../action/countyLogin';
import dosLogin from '../../action/dosLogin';


const submit = ({ dashboard, username, password }: any) => {
    switch (dashboard) {
        case 'sos': {
            dosLogin(username, password);
            break;
        }
        case 'county': {
            countyLogin(username, password);
            break;
        }
    }
};

export class LoginFormContainer extends React.Component<any, any> {
    public render() {
        return <LoginForm submit={ submit } />;
    }
}

export default connect()(LoginFormContainer);
