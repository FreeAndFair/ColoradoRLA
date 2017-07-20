import * as React from 'react';


export default class LoginForm extends React.Component<any, any> {
    public render() {
        return (
            <div>
                <label className='pt-label'>
                    Email
                    <input className='pt-input' type='text' />
                </label>
                <label className='pt-label'>
                    Password
                    <input className='pt-input' type='password' />
                </label>
                <button className='pt-primary'>Submit</button>
            </div>
        );
    }
}
