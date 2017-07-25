import { shallow } from 'enzyme';
import * as React from 'react';
import * as test from 'tape';

import { Redirect } from 'react-router-dom';

import LoginPage from '../../component/login/LoginPage';
import { LoginContainer } from './Login';


test('LoginContainer', s => {

    s.test('when logged out', t => {
        t.plan(2);

        const c = shallow(<LoginContainer loggedIn={ false } />);
        const r = c.find(Redirect);

        t.ok(!r.exists(), 'it does not redirect the user');

        t.ok(c.contains(<LoginPage />), 'it renders a <LoginPage>');
    });

    s.test('when logged in', t => {
        t.plan(2);

        const c = shallow(<LoginContainer loggedIn={ true } />);
        const r = c.find(Redirect);

        t.ok(r.exists(), 'it redirects the user');

        t.equals(r.first().prop('to'), '/', 'the redirect target is `/`');
    });

});
