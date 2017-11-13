import * as React from 'react';

import login1F from 'corla/action/login1F';


function isFormValid(form: FormFields): boolean {
    const { username, password } = form;

    return username.length > 0;
}

interface FormFields {
    password: string;
    username: string;
}

interface FormState {
    form: FormFields;
}

export default class PasswordForm extends React.Component<{}, FormState> {
    public state = {
        form: {
            password: '',
            username: '',
        },
    };

    public render() {
        const { form } = this.state;
        const disabled = !isFormValid(form);

        return (
            <div>
                <label className='pt-label'>
                    User ID
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
                <button
                    className='pt-button pt-intent-primary submit'
                    disabled={ disabled }
                    onClick={ this.buttonClick }>
                    Submit
                </button>
            </div>
        );
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
        const { username, password } = this.state.form;

        login1F(username, password);
    }
}
