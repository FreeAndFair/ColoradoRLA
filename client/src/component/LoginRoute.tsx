import * as React from 'react';
import {
    Redirect,
    Route,
} from 'react-router-dom';

import session from 'corla/session';


const LoginRoute = ({ page: Page, ...rest }: any) => {
    const render = (props: any) => {
        if (session.active()) {
            return <Page { ...props } />;
        }

        const from  = props.location.pathname || '/';
        const to = {
            pathname: '/login',
            state: { from },
        };
        return <Redirect to={ to } />;
    };

    return <Route render={ render } { ...rest } />;
};


export default LoginRoute;
