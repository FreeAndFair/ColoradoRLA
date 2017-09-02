import * as React from 'react';

import login2F from 'corla/action/login2F';


function isFormValid(form: any): boolean {
    const token = form.tokenParts.join('');

    return token !== '';
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
        },
    };

    public render() {
        const { loginChallenge } = this.props;
        const { form } = this.state;

        const disabled = !isFormValid(form);

        return (
            <div>
                <div className='pt-card'>
                    Enter challenge for user: { this.props.username }
                </div>
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

    private onTokenChange = (index: number) => (e: React.ChangeEvent<any>) => {
        const s = { ...this.state };
        s.form.tokenParts[index] = e.target.value;
        this.setState(s);
    }

    private buttonClick = (e: React.MouseEvent<HTMLButtonElement>) => {
        const { username, tokenParts } = this.state.form;

        const token = tokenParts.join(' ');

        login2F(this.props.username, token);
    }
}
