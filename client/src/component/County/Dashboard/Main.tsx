import * as React from 'react';

import FileUploadContainer from './FileUploadContainer';

import fetchReport from 'corla/action/county/fetchReport';


const AuditBoardInfo = ({ signedIn }: any) => {
    const icon = signedIn
               ? <span className='pt-icon pt-intent-success pt-icon-tick-circle' />
               : <span className='pt-icon pt-intent-danger pt-icon-error' />;

    const text = signedIn ? 'signed in' : 'not signed in';

    return (
        <div className='pt-card'>
            <span>{ icon } </span>
            Audit board is <strong>{ text }.</strong>
        </div>
    );
};

const Main = (props: any) => {
    const {
        auditBoardSignedIn,
        auditButtonDisabled,
        auditComplete,
        auditStarted,
        boardSignIn,
        canRenderReport,
        currentRoundNumber,
        name,
        signInButtonDisabled,
        startAudit,
    } = props;

    let directions = 'Please upload your Ballot Manifest and Cast Vote Records.';

    if (auditBoardSignedIn) {
        if (auditButtonDisabled) {
            directions = 'Please stand by for the state to begin the audit.';
        } else {
            directions = `You may proceed with Round ${currentRoundNumber} of the audit.`;
        }
    } else {
        if (!signInButtonDisabled) {
            directions = 'Please have the audit board sign in.';
        }
    }

    if (auditComplete) {
        directions = 'The audit is complete.';
    }

    const fileUploadContainer = auditStarted
                              ? <div />
                              : <FileUploadContainer />;

    const reportType = auditComplete
                     ? 'final'
                     : 'intermediate';

    return (
        <div className='county-main pt-card'>
            <h1>Hello, { name } County!</h1>
            <div>
                <div className='pt-card'>{ directions }</div>
                { fileUploadContainer }
                <AuditBoardInfo signedIn={ auditBoardSignedIn } />
                <div className='pt-card'>
                    <div>Click to download { reportType} audit report.</div>
                    <button
                        className='pt-button'
                        disabled={ !canRenderReport }
                        onClick={ fetchReport }>
                        Download
                    </button>
                </div>
                <button
                    className='pt-button pt-intent-primary'
                    disabled={ signInButtonDisabled }
                    onClick={ boardSignIn }>
                    <span className='pt-icon-standard pt-icon-people' />
                    <span> </span>
                    Audit Board
                </button>
                <button
                    className='pt-button pt-intent-primary'
                    disabled={ auditButtonDisabled }
                    onClick={ startAudit }>
                    <span className='pt-icon-standard pt-icon-eye-open' />
                    <span> </span>
                    Start Audit
                </button>
            </div>
        </div>
    );
};


export default Main;
