import * as React from 'react';

import FileUploadContainer from './FileUploadContainer';

import fetchReport from 'corla/action/county/fetchReport';

import FileDownloadButtons from 'corla/component/FileDownloadButtons';


interface AuditBoardInfoProps {
    signedIn: boolean;
}

const AuditBoardInfo = (props: AuditBoardInfoProps) => {
    const { signedIn } = props;

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

interface MainProps {
    auditBoardSignedIn: boolean;
    auditButtonDisabled: boolean;
    auditComplete: boolean;
    auditStarted: boolean;
    boardSignIn: OnClick;
    canRenderReport: boolean;
    county: CountyState;
    currentRoundNumber: number;
    name: string;
    signInButtonDisabled: boolean;
    startAudit: OnClick;
}

const Main = (props: MainProps) => {
    const {
        auditBoardSignedIn,
        auditButtonDisabled,
        auditComplete,
        auditStarted,
        boardSignIn,
        canRenderReport,
        county,
        currentRoundNumber,
        name,
        signInButtonDisabled,
        startAudit,
    } = props;

    let directions = 'You may now upload the Ballot Manifest and Cast Vote Records.';

    if (auditBoardSignedIn) {
        if (auditButtonDisabled) {
            directions = 'Please stand by for the state to begin the audit.';
        } else {
            if (currentRoundNumber) {
                directions = `You may proceed with Round ${currentRoundNumber} of the audit.`;
            } else {
                directions = 'Please wait for the next round to start.';
            }
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

    const fileDownloadButtons = auditStarted
                              ? <FileDownloadButtons status={ county } />
                              : <div />;

    const reportType = auditComplete
                     ? 'final'
                     : 'intermediate';

    return (
        <div className='county-main pt-card'>
            <h1>Hello, { name } County!</h1>
            <div>
                <div className='pt-card'>{ directions }</div>
                { fileUploadContainer }
                { fileDownloadButtons }
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
