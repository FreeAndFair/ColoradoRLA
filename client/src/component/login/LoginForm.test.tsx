import { shallow } from 'enzyme';
import * as React from 'react';
import * as test from 'tape';

import { Redirect } from 'react-router-dom';

import LoginForm, { FormFields } from './LoginForm';


const USERNAME = 'user@eample.com';
const PASSWORD = 'hunter2';
const SUBMIT = (form: FormFields) => { return; };


test('LoginForm', s => {
    s.test('initial state', t => {
        t.plan(3);

        const c = shallow(<LoginForm submit={ SUBMIT } />);

        const usernameField = c.find('input.username').first();
        t.equal(usernameField.prop('value'), '', 'the username field is empty');

        const passwordField = c.find('input.password').first();
        t.equal(passwordField.prop('value'), '', 'the password field is empty');

        const submitButton = c.find('button.submit').first();
        t.ok(submitButton.prop('disabled'), 'submit button is disabled');
    });

    s.test('with username only', t => {
        t.plan(3);

        const c = shallow(<LoginForm submit={ SUBMIT } />);
        c.setState({ form: { username: USERNAME, password: '' } });

        const usernameField = c.find('input.username').first();
        t.equal(usernameField.prop('value'), USERNAME, 'the username field is non-empty');

        const passwordField = c.find('input.password').first();
        t.equal(passwordField.prop('value'), '', 'the password field is empty');

        const submitButton = c.find('button.submit').first();
        t.ok(submitButton.prop('disabled'), 'submit button is disabled');
    });

    s.test('with password only', t => {
        t.plan(3);

        const c = shallow(<LoginForm submit={ SUBMIT } />);
        c.setState({ form: { username: '', password: PASSWORD } });

        const usernameField = c.find('input.username').first();
        t.equal(usernameField.prop('value'), '',
                'the username field is empty');

        const passwordField = c.find('input.password').first();
        t.equal(passwordField.prop('value'), PASSWORD,
                'the password field is non-empty');

        const submitButton = c.find('button.submit').first();
        t.ok(submitButton.prop('disabled'), 'submit button is disabled');
    });

    s.test('with username and password', t => {
        t.plan(3);

        const c = shallow(<LoginForm submit={ SUBMIT } />);
        c.setState({ form: { username: USERNAME, password: PASSWORD } });

        const usernameField = c.find('input.username').first();
        t.equal(usernameField.prop('value'), USERNAME,
                'the username field is non-empty');

        const passwordField = c.find('input.password').first();
        t.equal(passwordField.prop('value'), PASSWORD,
                'the password field is non-empty');

        const submitButton = c.find('button.submit').first();
        t.ok(!submitButton.prop('disabled'), 'submit button is enabled');
    });

    s.test('submit button triggers submit action', t => {
        t.plan(1);

        const FORM = { username: USERNAME, password: PASSWORD };

        const submit = (form: FormFields) => {
            t.deepEqual(form, FORM, 'the form fields are submitted');
        };

        const c = shallow(<LoginForm submit={ submit } />);
        c.setState({ form: FORM });

        c.find('button.submit').simulate('click');

        t.end();
    });
});
