import * as React from 'react';


export interface FormFields {
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
                <button
                    disabled={ disabled }
                    className='pt-primary submit'
                    onClick={ this.buttonClick }>
                    Submit
                </button>
            </div>
        );
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
        return;
    }
}
