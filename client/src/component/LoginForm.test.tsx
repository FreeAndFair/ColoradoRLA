import { shallow } from 'enzyme';
import * as React from 'react';
import * as test from 'tape';

import { Redirect } from 'react-router-dom';

import LoginForm, { FormFields } from './LoginForm';


const EMAIL = 'user@eample.com';
const PASSWORD = 'hunter2';
const SUBMIT = (form: FormFields) => { return; };


test('LoginForm', s => {
    s.test('initial state', t => {
        t.plan(3);

        const c = shallow(<LoginForm submit={ SUBMIT } />);

        const emailField = c.find('input.email').first();
        t.equal(emailField.prop('value'), '', 'the email field is empty');

        const passwordField = c.find('input.password').first();
        t.equal(passwordField.prop('value'), '', 'the password field is empty');

        const submitButton = c.find('button.submit').first();
        t.ok(submitButton.prop('disabled'), 'submit button is disabled');
    });

    s.test('with email only', t => {
        t.plan(3);

        const c = shallow(<LoginForm submit={ SUBMIT } />);
        c.setState({ form: { email: EMAIL, password: '' } });

        const emailField = c.find('input.email').first();
        t.equal(emailField.prop('value'), EMAIL, 'the email field is non-empty');

        const passwordField = c.find('input.password').first();
        t.equal(passwordField.prop('value'), '', 'the password field is empty');

        const submitButton = c.find('button.submit').first();
        t.ok(submitButton.prop('disabled'), 'submit button is disabled');
    });

    s.test('with password only', t => {
        t.plan(3);

        const c = shallow(<LoginForm submit={ SUBMIT } />);
        c.setState({ form: { email: '', password: PASSWORD } });

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

        const c = shallow(<LoginForm submit={ SUBMIT } />);
        c.setState({ form: { email: EMAIL, password: PASSWORD } });

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
