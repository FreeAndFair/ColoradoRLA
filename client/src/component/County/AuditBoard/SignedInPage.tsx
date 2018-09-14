import * as React from 'react';

import CountyNav from '../Nav';

import auditBoardSignOut from 'corla/action/county/auditBoardSignOut';


interface PageProps {
    auditBoardStatus: AuditBoardStatus;
    auditBoardIndex: number;
    auditBoardStartOrContinue: OnClick;
    countyName: string;
    hasAuditedAnyBallot: boolean;
}

const SignedInPage = (props: PageProps) => {
    const {
        auditBoardStatus,
        auditBoardIndex,
        auditBoardStartOrContinue,
        countyName,
        hasAuditedAnyBallot,
    } = props;

    const members = auditBoardStatus.members;

    const startOrContinueText = hasAuditedAnyBallot ? 'Continue Audit' : 'Start Audit';

    return (
        <div>
            <CountyNav />
            <div>
                <h2>Audit Board { auditBoardIndex + 1 }</h2>
                <div className='pt-card'>
                    <h5>The Audit Board members below are signed in.
                    To sign the Audit Board out, click the "Sign Out" button below.</h5>
                </div>
            </div>
            <div className='pt-card'>
                <h4>Board Member 1:</h4>
                <div>
                    Name: { members[0].firstName } { members[0].lastName }
                </div>
                <div>
                    Political party: { members[0].party }
                </div>
            </div>
            <div className='pt-card'>
                <h4>Board Member 2:</h4>
                <div>
                    Name: { members[1].firstName } { members[1].lastName }
                </div>
                <div>
                    Political party: { members[1].party }
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
                    onClick={ () => auditBoardSignOut(auditBoardIndex) }>
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
