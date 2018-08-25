import * as React from 'react';

import AuditBoardNumberSelector from 'corla/component/County/Dashboard/AuditBoardNumberSelector';

import FileUploadContainer from './FileUploadContainer';

import downloadCvrsToAuditCsv from 'corla/action/county/downloadCvrsToAuditCsv';

import fetchReport from 'corla/action/county/fetchReport';

import FileDownloadButtons from 'corla/component/FileDownloadButtons';


interface AuditBoardButtonsProps {
    numberOfBoards: number;
    isShown: boolean;
}

const AuditBoardButtons = (props: AuditBoardButtonsProps) => {
    const { isShown, numberOfBoards } = props;

    if (!isShown) {
        return null;
    }

    const boardButton = (boardIndex: number) => {
        return (
            <button className='pt-button pt-intent-primary pt-icon-people'
                    key={ boardIndex.toString() }>
                Audit Board { boardIndex + 1 }
            </button>
        );
    };

    let buttons = [];
    for (let i = 0; i < numberOfBoards; i++) {
        buttons.push(boardButton(i));
    }

    return (
        <div className='pt-card'>
            <h5 className='pt-ui-text-large'>Sign in to an audit board</h5>
            <div className="pt-button-group pt-large corla-spaced">{ buttons }</div>
        </div>
    );
}

interface MainProps {
    auditBoardSignedIn: boolean;
    startAuditButtonDisabled: boolean;
    auditComplete: boolean;
    auditStarted: boolean;
    boardSignIn: OnClick;
    canRenderReport: boolean;
    countyState: County.AppState;
    currentRoundNumber: number;
    name: string;
    auditBoardButtonDisabled: boolean;
    startAudit: OnClick;
}

const Main = (props: MainProps) => {
    const {
        auditBoardSignedIn,
        startAuditButtonDisabled,
        auditComplete,
        auditStarted,
        boardSignIn,
        canRenderReport,
        countyState,
        currentRoundNumber,
        name,
        auditBoardButtonDisabled,
        startAudit,
    } = props;

    let directions = 'Upload the ballot manifest and cast vote record (CVR) files. These need to be CSV files.'
                   + '\n\nAfter uploading the files wait for an email from the Department of State saying that you can'
                   + ' continue the audit.';

    if (auditBoardSignedIn) {
        if (startAuditButtonDisabled) {
            directions = 'Wait for the audit to start.';
        } else {
            if (currentRoundNumber) {
                directions = `You can start round ${currentRoundNumber} of the audit.`;
            } else {
                directions = 'You have completed the round. More ballots need to be audited.'
                           + ' Wait for the next round to start.';
            }
        }
    } else {
        if (!auditBoardButtonDisabled) {
            directions = 'The Department of State has defined the audit and your list of ballots is now available for'
                       + ' download on this page. The audit board(s) must sign in to advance to the audit.';
        }
    }

    if (auditComplete) {
        directions = 'You have successfully completed the Risk-Limiting Audit! Print all pages of your final audit'
                   + ' report. Have the judges and county clerk sign the last page of the report and email it to'
                   + ' RLA@sos.state.co.us. You can now proceed to canvass!';
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
                    <div className='pt-ui-text-large'>{ reportType } audit report (CSV)</div>
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
                <AuditBoardNumberSelector auditBoardCount={ countyState.auditBoardCount || 1 }
                                          numberOfBallotsToAudit={ countyState.ballotsRemainingInRound }
                                          isShown={ !auditBoardButtonDisabled }
                                          isEnabled={ !countyState.auditBoardCount } />
                <AuditBoardButtons numberOfBoards={ countyState.auditBoardCount || 1 }
                                   isShown={ countyState.auditBoardCount != null } />
            </div>
        </div>
    );
};


export default Main;
