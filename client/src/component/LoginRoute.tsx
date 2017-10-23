import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import {
    Redirect,
    Route,
    RouteProps,
} from 'react-router-dom';

import session from 'corla/session';


interface LoginRouteProps extends RouteProps {
    page: React.ComponentClass,
}

function LoginRoute(props: LoginRouteProps) {
    const { page: Page, ...rest } = props;

    function render(props: RouteComponentProps<any>) {
        if (session.active()) {
            return <Page { ...props } />;
        }

        const from = props.location.pathname || '/';
        const to = {
            pathname: '/login',
            state: { from },
        };
        return <Redirect to={ to } />;
    };

    return <Route exact render={ render } { ...rest } />;
};


export default LoginRoute;
