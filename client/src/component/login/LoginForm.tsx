import * as React from 'react';

import { Radio, RadioGroup } from '@blueprintjs/core';


type Dashboard = 'sos' | 'county';

export interface FormFields {
    dashboard: Dashboard,
    email: string;
    password: string;
}

function isFormValid(form: FormFields): boolean {
    const { email, password } = form;

    return (email.length > 0) && (password.length > 0);
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
                email: '',
                password: '',
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
                    <input className='pt-input email'
                           type='text'
                           onChange={ this.onEmailChange }
                           value={ form.email } />
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
        s.form.email = e.target.value;
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
