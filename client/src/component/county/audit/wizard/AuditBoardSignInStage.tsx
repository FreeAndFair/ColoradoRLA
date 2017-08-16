import * as React from 'react';

import AuditBoardSignInForm from './AuditBoardSignInForm';


const AuditBoardSignInStage = (props: any) => {
    const { auditBoard, county, nextStage, updateBoardMember } = props;

    const forms: any = { auditBoard: [] };

    const submit = () => {
        console.log()
        updateBoardMember();
        nextStage();
    };

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
