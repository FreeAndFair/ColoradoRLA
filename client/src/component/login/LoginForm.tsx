import * as React from 'react';

import { Radio, RadioGroup } from '@blueprintjs/core';


export interface FormFields {
    dashboard: Dashboard;
    password: string;
    username: string;
}

function isFormValid(form: FormFields): boolean {
    const { username, password } = form;

    return (username.length > 0) && (password.length > 0);
}

interface LoginFormProps {
    submit: (form: FormFields) => void;
}

interface LoginFormState {
    form: FormFields;
}

export default class LoginForm
extends React.Component<LoginFormProps, LoginFormState> {
    constructor(props: LoginFormProps) {
        super(props);

        this.state = {
            form: {
                dashboard: 'sos',
                password: '',
                username: '',
            },
        };
    }

    public render() {
        const { form } = this.state;
        const disabled = !isFormValid(form);

        return (
            <div>
                <label className='pt-label'>
                    Email
                    <input className='pt-input username'
                           type='text'
                           onChange={ this.onEmailChange }
                           value={ form.username } />
                </label>
                <label className='pt-label'>
                    Password
                    <input className='pt-input password'
                           type='password'
                           onChange={ this.onPasswordChange }
                           value={ form.password } />
                </label>
                <RadioGroup
                    label='Dashboard'
                    onChange={ this.onDashboardChange }
                    selectedValue={ this.state.form.dashboard }>
                    <Radio label='Secretary of State' value='sos' />
                    <Radio label='County' value='county' />
                </RadioGroup>
                <button
                    disabled={ disabled }
                    className='pt-primary submit'
                    onClick={ this.buttonClick }>
                    Submit
                </button>
            </div>
        );
    }

    private onDashboardChange = (e: React.ChangeEvent<any>) => {
        const s = { ...this.state };
        s.form.dashboard = e.target.value;
        this.setState(s);
    }

    private onEmailChange = (e: React.ChangeEvent<any>) => {
        const s = { ...this.state };
        s.form.username = e.target.value;
        this.setState(s);
    }

    private onPasswordChange = (e: React.ChangeEvent<any>) => {
        const s = { ...this.state };
        s.form.password = e.target.value;
        this.setState(s);
    }

    private buttonClick = (e: React.MouseEvent<HTMLButtonElement>) => {
        this.props.submit(this.state.form);
    }
}
