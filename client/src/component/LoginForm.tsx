import * as React from 'react';


export default class LoginForm extends React.Component<any, any> {
    constructor() {
        super();

        this.state = { valid: false };
    }

    public render() {
        const disabled = !this.state.valid;

        return (
            <div>
                <label className='pt-label'>
                    Email
                    <input className='pt-input email' type='text' />
                </label>
                <label className='pt-label'>
                    Password
                    <input className='pt-input password' type='password' />
                </label>
                <button disabled={ disabled } className='pt-primary submit'>
                    Submit
                </button>
            </div>
        );
    }
}
