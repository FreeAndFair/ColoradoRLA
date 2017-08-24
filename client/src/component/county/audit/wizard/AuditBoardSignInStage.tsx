import * as React from 'react';

import AuditBoardSignInForm from './AuditBoardSignInForm';

import establishAuditBoard from '../../../../action/establishAuditBoard';


function validateElector(elector: any) {
    return elector.firstName
        && elector.lastName
        && elector.party;
}

function validateForm(auditBoard: any) {
    return validateElector(auditBoard[0])
        && validateElector(auditBoard[1]) ;
}

class AuditBoardSignInStage extends React.Component<any, any> {
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
        const { auditBoard, county, nextStage } = this.props;

        if (!auditBoard) {
            return <div />;
        }

        const submit = () => {
            establishAuditBoard(this.state.form);
            nextStage();
        };

        if (auditBoard.length === 2) {
            return (
                <div>
                    <div>
                        <h2>Audit Board Sign-in</h2>
                        <div className='pt-card'>
                            Enter the full names and party affiliations of each member of
                            the Acme County Audit Board who will be conducting this audit
                            today.
                        </div>
                    </div>
                    <div className='pt-card'>
                        <h4>Board Member 1:</h4>
                        <div>
                            Name: { auditBoard[0].firstName } { auditBoard[0].lastName }
                        </div>
                        <div>
                            Political party: { auditBoard[0].party }
                        </div>
                    </div>
                    <div className='pt-card'>
                        <h4>Board Member 2:</h4>
                        <div>
                            Name: { auditBoard[1].firstName } { auditBoard[1].lastName }
                        </div>
                        <div>
                            Political party: { auditBoard[1].party }
                        </div>
                    </div>
                    <div>
                    </div>
                    <button className='pt-button pt-intent-primary' onClick={ nextStage }>
                        Submit & Next
                    </button>
                </div>
            );
        }

        const disableButton = !validateForm(this.state.form);

        return (
            <div>
                <div>
                    <h2>Audit Board Sign-in</h2>
                    <div className='pt-card'>
                        Enter the full names and party affiliations of each member of
                        the Acme County Audit Board who will be conducting this audit
                        today:
                    </div>
                </div>
                <AuditBoardSignInForm
                    elector={ this.state.form[0] }
                    onFirstNameChange={ this.onFirstNameChange(0) }
                    onLastNameChange={ this.onLastNameChange(0) }
                    onPartyChange={ this.onPartyChange(0) }
                />
                <AuditBoardSignInForm
                    elector={ this.state.form[1] }
                    onFirstNameChange={ this.onFirstNameChange(1) }
                    onLastNameChange={ this.onLastNameChange(1) }
                    onPartyChange={ this.onPartyChange(1) }
                />
                <button
                    className='pt-button pt-intent-primary'
                    disabled={ disableButton }
                    onClick={ submit }>
                    Submit & Next
                </button>
            </div>
        );
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

    private onPartyChange = (index: number) => (e: any) => {
        const s = { ...this.state };

        const party = e.target.value;
        s.form[index].party = party;

        this.setState(s);
    }
}


export default AuditBoardSignInStage;
