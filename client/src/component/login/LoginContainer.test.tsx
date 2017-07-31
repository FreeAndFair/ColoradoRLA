import { shallow } from 'enzyme';
import * as React from 'react';
import * as test from 'tape';

import { Redirect } from 'react-router-dom';

import { LoginContainer } from './LoginContainer';
import LoginPage from './LoginPage';


const routeProps = () => ({ location: { state: { from: '/' } } });


test('LoginContainer', s => {

    s.test('when logged out', t => {
        t.plan(2);

        const c = shallow(<LoginContainer loggedIn={ false } { ...routeProps() } />);
        const r = c.find(Redirect);

        t.ok(!r.exists(), 'it does not redirect the user');

        t.ok(c.contains(<LoginPage />), 'it renders a <LoginPage>');
    });

    s.test('when logged in', t => {
        t.plan(2);

        const c = shallow(<LoginContainer loggedIn={ true } { ...routeProps() } />);
        const r = c.find(Redirect);

        t.ok(r.exists(), 'it redirects the user');

        t.equals(r.first().prop('to'), '/', 'the redirect target is `/`');
    });

});
