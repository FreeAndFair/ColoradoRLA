import * as React from 'react';

import login2F from '../../../action/login2F';


function isFormValid(form: any): boolean {
    const { username } = form;

    return username.length > 0;
}

const ChallengeForm = (props: any) => {
    const { loginChallenge, onTokenChange, tokenParts } = props;

    const challengeFields = loginChallenge.map((box: any, index: number) => {
        const text = box.join('');

        return (
            <div key={ `${text}${index}` }>
                <label className='pt-label'>
                    { text }
                    <input className='pt-input password'
                           type='password'
                           onChange={ onTokenChange(index) }
                           value={ tokenParts[index] || '' } />
                </label>
            </div>
        );
    });

    return (
        <div className='pt-card'>
            <div><strong>Grid Challenge:</strong></div>
            <div className='pt-card'>
                { challengeFields }
            </div>
        </div>
    );
};

export default class LoginForm extends React.Component<any, any> {
    public state: any = {
        form: {
            tokenParts: [],
            username: '',
        },
    };

    public render() {
        const { loginChallenge } = this.props;
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
                <ChallengeForm
                    loginChallenge={ loginChallenge }
                    onTokenChange={ this.onTokenChange }
                    tokenParts={ form.tokenParts } />
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

    private onTokenChange = (index: number) => (e: React.ChangeEvent<any>) => {
        const s = { ...this.state };
        s.form.tokenParts[index] = e.target.value;
        this.setState(s);
    }

    private buttonClick = (e: React.MouseEvent<HTMLButtonElement>) => {
        const { username, tokenParts } = this.state.form;

        const token = tokenParts.join('');

        login2F(username, token);
    }
}
