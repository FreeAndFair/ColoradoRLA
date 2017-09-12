import * as React from 'react';

import CountyNav from '../Nav';

import auditBoardSignOut from 'corla/action/county/auditBoardSignOut';


const SignedInPage = (props: any) => {
    const {
        auditBoard,
        auditBoardStartOrContinue,
        countyName,
        hasAuditedAnyBallot,
    } = props;

    const startOrContinueText = hasAuditedAnyBallot ? 'Continue Audit' : 'Start Audit';

    return (
        <div>
            <CountyNav />
            <div>
                <h2>Audit Board</h2>
                <div className='pt-card'>
                    Enter the full names and party affiliations of each member of
                    the { countyName } County Audit Board who will be conducting this
                    audit today.
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
            <button
                disabled={ true }
                className='pt-button pt-intent-primary'>
                Submit
            </button>
            <button
                className='pt-button pt-intent-primary'
                onClick={ auditBoardSignOut }>
                Sign Out
            </button>
            <button
                className='pt-button pt-intent-primary'
                onClick={ auditBoardStartOrContinue }>
                { startOrContinueText }
            </button>
        </div>
    );
};


export default SignedInPage;
