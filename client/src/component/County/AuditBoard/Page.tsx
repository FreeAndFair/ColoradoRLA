import * as React from 'react';

import CountyNav from '../Nav';

import SignInForm from './SignInForm';

import auditBoardSignIn from 'corla/action/county/auditBoardSignIn';

import isValidAuditBoard from 'corla/selector/county/isValidAuditBoard';


interface PageProps {
    auditBoardIndex: number;
    countyName: string;
}

interface PageState {
    form: AuditBoard;
}

class AuditBoardSignInPage extends React.Component<PageProps, PageState> {
    public state = {
        form: [
            {
                firstName: '',
                lastName: '',
                party: '',
            },
            {
                firstName: '',
                lastName: '',
                party: '',
            },
        ],
    };

    public render() {
        const {
          auditBoardIndex,
          countyName,
        } = this.props;

        const submit = () => {
          auditBoardSignIn(auditBoardIndex, this.state.form);
        };

        const disableButton = !isValidAuditBoard(this.state.form);

        return (
            <div>
                <CountyNav />
                <div>
                    <h2>Audit Board { auditBoardIndex + 1 }</h2>
                    <div className='pt-card'>
                        <span className='pt-ui-text-large'>
                            Enter the full names and party affiliations of each
                            member of the { countyName } County Audit Board
                            { ' ' + (auditBoardIndex + 1) } who will be
                            conducting this audit today.
                         </span>
                    </div>
                </div>
                <SignInForm
                    elector={ this.state.form[0] }
                    onFirstNameChange={ this.onFirstNameChange(0) }
                    onLastNameChange={ this.onLastNameChange(0) }
                    onPartyChange={ this.onPartyChange(0) }
                    onTextConfirm={ this.onTextConfirm }
                />
                <SignInForm
                    elector={ this.state.form[1] }
                    onFirstNameChange={ this.onFirstNameChange(1) }
                    onLastNameChange={ this.onLastNameChange(1) }
                    onPartyChange={ this.onPartyChange(1) }
                    onTextConfirm={ this.onTextConfirm }
                />
                <button
                    className='pt-button pt-intent-primary'
                    disabled={ disableButton }
                    onClick={ submit }>
                    Sign In
                </button>
            </div>
        );
    }

    private onTextConfirm = () => {
        const s = { ...this.state };

        s.form[0].firstName = s.form[0].firstName.trim();
        s.form[0].lastName = s.form[0].lastName.trim();

        s.form[1].firstName = s.form[1].firstName.trim();
        s.form[1].lastName = s.form[1].lastName.trim();

        this.setState(s);
    }

    private onFirstNameChange = (index: number) => (name: string) => {
        const s = { ...this.state };

        s.form[index].firstName = name;

        this.setState(s);
    }

    private onLastNameChange = (index: number) => (name: string) => {
        const s = { ...this.state };

        s.form[index].lastName = name;

        this.setState(s);
    }

    private onPartyChange = (index: number) => (e: React.ChangeEvent<any>) => {
        const s = { ...this.state };

        const party = e.target.value;
        s.form[index].party = party;

        this.setState(s);
    }
}


export default AuditBoardSignInPage;
