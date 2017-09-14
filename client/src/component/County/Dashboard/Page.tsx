import * as React from 'react';
import { Link } from 'react-router-dom';

import CountyNav from '../Nav';

import Info from './Info';
import Main from './Main';


const CountyDashboardPage = (props: any) => {
    const {
        allRoundsComplete,
        auditBoardSignedIn,
        auditComplete,
        auditStarted,
        boardSignIn,
        canAudit,
        canRenderReport,
        canSignIn,
        contests,
        county,
        countyInfo,
        countyDashboardRefresh,
        currentRoundNumber,
        finishAudit,
        startAudit,
    } = props;
    const {
        ballots,
        ballotManifestHash,
        cvrExportHash,
        startTimestamp,
        status,
    } = county;

    const info = { auditDate: startTimestamp };

    const auditButtonDisabled = !canAudit || auditComplete;
    const signInButtonDisabled = !canSignIn;

    return (
        <div className='county-root'>
            <CountyNav />
            <div>
                <Main auditComplete={ auditComplete }
                      auditStarted={ auditStarted }
                      auditBoardSignedIn={ auditBoardSignedIn }
                      boardSignIn={ boardSignIn }
                      canRenderReport={ canRenderReport }
                      currentRoundNumber={ currentRoundNumber }
                      auditButtonDisabled={ auditButtonDisabled }
                      name={ countyInfo.name }
                      signInButtonDisabled={ signInButtonDisabled }
                      startAudit={ startAudit } />
                <Info info={ countyInfo }
                      contests={ contests }
                      county={ county }
                      currentRoundNumber={ currentRoundNumber }/>
            </div>
        </div>
    );
};


export default CountyDashboardPage;
