import * as React from 'react';

import AuditBoardSignInForm from './AuditBoardSignInForm';


const AuditBoardSignInStage = (props: any) => {
    const { auditBoard, county, establishAuditBoard, nextStage } = props;

    const forms: any = { auditBoard: [] };

    if (!auditBoard) {
        return <div />;
    }

    const submit = () => {
        establishAuditBoard(forms.auditBoard);
        nextStage();
    };

    if (auditBoard.length == 2) {
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
                boardMemberIndex={ 0 }
                forms={ forms }
            />
            <AuditBoardSignInForm
                boardMemberIndex={ 1 }
                forms={ forms }
            />
            <button className='pt-button pt-intent-primary' onClick={ submit }>
                Submit & Next
            </button>
        </div>
    );
};


export default AuditBoardSignInStage;
