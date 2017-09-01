import * as React from 'react';


function isFormValid(form: any): boolean {
    const { username } = form;

    return username.length > 0;
}

export default class LoginForm extends React.Component<any, any> {
    public state = {
        form: {
            token: '',
            username: '',
        },
    };

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
                    Token
                    <input className='pt-input password'
                           type='password'
                           onChange={ this.onTokenChange }
                           value={ form.token } />
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
        s.form.username = e.target.value;
        this.setState(s);
    }

    private onTokenChange = (e: React.ChangeEvent<any>) => {
        const s = { ...this.state };
        s.form.token = e.target.value;
        this.setState(s);
    }

    private buttonClick = (e: React.MouseEvent<HTMLButtonElement>) => {
        this.props.submit(this.state.form);
    }
}
