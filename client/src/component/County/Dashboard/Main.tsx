import * as React from 'react';

import FileUploadContainer from './FileUploadContainer';

import downloadCvrsToAuditCsv from 'corla/action/county/downloadCvrsToAuditCsv';

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
    countyState: County.AppState;
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
        countyState,
        currentRoundNumber,
        name,
        signInButtonDisabled,
        startAudit,
    } = props;

    let directions = "Upload the ballot manifest and cast voter records (CVR) files. These need to be CSV files. \n\nAfter uploading the files wait for an email from the Department of State saying that you can continue the audit.";

    if (auditBoardSignedIn) {
        if (auditButtonDisabled) {
            directions = 'Wait for the audit to start.';
        } else {
            if (currentRoundNumber) {
                directions = `You can start round ${currentRoundNumber} of the audit.`;
            } else {
                directions = 'You have completed the round. More ballots need to be audited. Wait for the next round to start.';
            }
        }
    } else {
        if (!signInButtonDisabled) {
            directions = 'The Department of State has defined the audit and your list of ballots is now available for download on this page. The audit board must sign in to advance to the audit.';
        }
    }

    if (auditComplete) {
        directions = 'You have successfully completed the Risk-Limiting Audit! Print all pages of your final audit report. Have the judges and county clerk sign the last page of the report and email it to RLA@sos.state.co.us. You can now proceed to canvass!';
    }

    const fileUploadContainer = auditStarted
                              ? <div />
                              : <FileUploadContainer />;

    const fileDownloadButtons = auditStarted
                              ? <FileDownloadButtons status={ countyState } />
                              : <div />;

    const reportType = auditComplete
                     ? 'Final'
                     : 'Intermediate';

    const downloadCsv = () => downloadCvrsToAuditCsv(currentRoundNumber);

    return (
        <div className='county-main pt-card'>
            <h1>Hello, { name } County!</h1>
            <div>
                <div className='pt-card'><h3>{ directions }</h3></div>
                { fileUploadContainer }
                { fileDownloadButtons }
                <div className='pt-card'>
                    <div className='pt-ui-text-large'>{ reportType} audit report (CSV)</div>
                    <button
                        className='pt-button  pt-intent-primary'
                        disabled={ !canRenderReport }
                        onClick={ fetchReport }>
                        Download
                    </button>
                </div>
                <div className='pt-card'>
                    <div className='pt-ui-text-large'>List of ballots to audit (CSV)</div>
                    <button
                        className='pt-button  pt-intent-primary'
                        disabled={ !canRenderReport }
                        onClick={ downloadCsv }>
                        Download
                    </button>
                </div>
                <div>
                  <AuditBoardInfo signedIn={ auditBoardSignedIn } />                    
                  <button
                      className='pt-button pt-intent-primary audit'
                      disabled={ signInButtonDisabled }
                      onClick={ boardSignIn }>
                      <span className='pt-icon-standard pt-icon-people' />
                      <span> </span>
                      Audit Board
                  </button>
                  <br/>
                  <p/>
                  <button
                      className='pt-button pt-intent-primary audit'
                      disabled={ auditButtonDisabled }
                      onClick={ startAudit }>
                      <span className='pt-icon-standard pt-icon-eye-open' />
                      <span> </span>
                      Start Audit
                  </button>
                </div>
            </div>
        </div>
    );
};


export default Main;
