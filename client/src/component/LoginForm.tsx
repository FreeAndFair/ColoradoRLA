import * as React from 'react';


interface FormFields {
    email: string;
    password: string;
}

function isFormValid(form: FormFields): boolean {
    return false;
}

export default class LoginForm extends React.Component<any, any> {
    constructor() {
        super();

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
                           value={ form.email } />
                </label>
                <label className='pt-label'>
                    Password
                    <input className='pt-input password'
                           type='password'
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

    private buttonClick(e: React.MouseEvent<HTMLButtonElement>) {
        return;
    }
}
