import * as React from 'react';

import login2F from 'corla/action/login2F';


function isFormValid(form: Form): boolean {
    return form.token !== '';
}

function formatChallenge(challenge: Array<[string, string]>): string {
    return challenge.map(box => {
        return '[' + box.join('') + ']';
    }).join(' ');
}

interface ChallengeFormProps {
    loginChallenge: LoginChallenge;
    onTokenChange: () => (e: React.ChangeEvent<any>) => void;
    token: string;
}

const ChallengeForm = (props: ChallengeFormProps) => {
    const { loginChallenge, onTokenChange, token } = props;
    return (
        <div className='pt-card'>
            <label className='pt-label'>
                <input className='pt-input password'
                       type='password'
                       onChange={ onTokenChange() }
                       value={ token || '' } />
            </label>
        </div>);
};

interface FormProps {
    loginChallenge: LoginChallenge;
    username: string;
}

interface FormState {
    form: Form;
}

interface Form {
    token: string;
    username: string;
}

export default class SecondFactorForm extends React.Component<FormProps, FormState> {
    public state: FormState = {
        form: {
            token: '',
            username: '',
        },
    };

    public render() {
        const { loginChallenge, username } = this.props;
        const { form } = this.state;
        const disabled = !isFormValid(form);
        const challenge = formatChallenge(loginChallenge);

        return (
            <div>
                <div className='pt-card'>
                <div><strong>Grid Challenge:</strong></div>
                Enter a response to the grid challenge {challenge} for the user: {username}
                </div>
                <ChallengeForm
                    loginChallenge={ loginChallenge }
                    onTokenChange={ this.onTokenChange }
                    token={ form.token } />
                <button
                    className='pt-button pt-intent-primary submit'
                    disabled={ disabled }
                    onClick={ this.buttonClick }>
                    Submit
                </button>
            </div>
        );
    }

    private onTokenChange = () => (e: React.ChangeEvent<any>) => {
        const s = { ...this.state };
        s.form.token = e.target.value.replace(/\s*/g, '');
        this.setState(s);
    }

    private buttonClick = (e: React.MouseEvent<HTMLButtonElement>) => {
        const { username, token } = this.state.form;
        login2F(this.props.username.toLowerCase(), token.split('').join(' '));
    }
}
