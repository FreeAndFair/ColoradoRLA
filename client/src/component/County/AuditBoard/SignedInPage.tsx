import * as React from 'react';

import CountyNav from '../Nav';

import auditBoardSignOut from 'corla/action/county/auditBoardSignOut';


interface PageProps {
    auditBoard: AuditBoard;
    auditBoardStartOrContinue: OnClick;
    countyName: string;
    hasAuditedAnyBallot: boolean;
}

const SignedInPage = (props: PageProps) => {
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
                    <h5>The Audit Board members below are signed in.
                    To sign the Audit Board out, click the "Sign Out" button below.</h5>
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
                <button
                    disabled={ true }
                    className='pt-button pt-intent-primary pt-breadcrumb'>
                    Submit
                </button>
                <button
                    className='pt-button pt-intent-primary pt-breadcrumb'
                    onClick={ auditBoardSignOut }>
                    Sign Out
                </button>
                <button
                    className='pt-button pt-intent-primary pt-breadcrumb'
                    onClick={ auditBoardStartOrContinue }>
                    { startOrContinueText }
                </button>
            </div>
        </div>
    );
};


export default SignedInPage;
