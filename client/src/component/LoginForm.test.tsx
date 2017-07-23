import { shallow } from 'enzyme';
import * as React from 'react';
import * as test from 'tape';

import { Redirect } from 'react-router-dom';

import LoginForm from './LoginForm';


const EMAIL = 'user@eample.com';
const PASSWORD = 'hunter2';

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

    s.test('with email only', t => {
        t.plan(3);

        const c = shallow(<LoginForm />);

        const nextState = c.state();
        nextState.form.email = EMAIL;
        c.setState(nextState);

        const emailField = c.find('input.email').first();
        t.equal(emailField.prop('value'), EMAIL, 'the email field is non-empty');

        const passwordField = c.find('input.password').first();
        t.equal(passwordField.prop('value'), '', 'the password field is empty');

        const submitButton = c.find('button.submit').first();
        t.ok(submitButton.prop('disabled'), 'submit button is disabled');
    });

    s.test('with password only', t => {
        t.plan(3);

        const c = shallow(<LoginForm />);

        const nextState = c.state();
        nextState.form.password = PASSWORD;
        c.setState(nextState);

        const emailField = c.find('input.email').first();
        t.equal(emailField.prop('value'), '',
                'the email field is empty');

        const passwordField = c.find('input.password').first();
        t.equal(passwordField.prop('value'), PASSWORD,
                'the password field is non-empty');

        const submitButton = c.find('button.submit').first();
        t.ok(submitButton.prop('disabled'), 'submit button is disabled');
    });

    s.test('with email and password', t => {
        t.plan(3);

        const c = shallow(<LoginForm />);

        const nextState = c.state();
        nextState.form = { email: EMAIL, password: PASSWORD };
        c.setState(nextState);

        const emailField = c.find('input.email').first();
        t.equal(emailField.prop('value'), EMAIL,
                'the email field is non-empty');

        const passwordField = c.find('input.password').first();
        t.equal(passwordField.prop('value'), PASSWORD,
                'the password field is non-empty');

        const submitButton = c.find('button.submit').first();
        t.ok(!submitButton.prop('disabled'), 'submit button is enabled');
    });
});
