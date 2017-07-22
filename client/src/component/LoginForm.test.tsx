import { shallow } from 'enzyme';
import * as React from 'react';
import * as test from 'tape';

import { Redirect } from 'react-router-dom';

import LoginForm from './LoginForm';


test('LoginForm', s => {

    s.test('initial state', t => {
        t.plan(3);

        const c = shallow(<LoginForm />);

        const emailField = c.find('input.email').first();
        t.equal(emailField.prop('value'), '', 'the email field is empty');

        const passwordField = c.find('input.password').first();
        t.equal(passwordField.prop('value'), '', 'the password field is empty');

        const submitButton = c.find('button.submit').first();
        t.ok(submitButton.prop('disabled'), 'submit button is disabled');
    });

});
