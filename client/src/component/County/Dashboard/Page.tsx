import * as React from 'react';
import { Link } from 'react-router-dom';

import { History } from 'history';

import LicenseFooter from 'corla/component/LicenseFooter';

import CountyNav from '../Nav';

import Info from './Info';
import Main from './Main';


interface PageProps {
    auditBoardSignedIn: boolean;
    auditComplete: boolean;
    auditStarted: boolean;
    canAudit: boolean;
    canRenderReport: boolean;
    canSignIn: boolean;
    contests: County.ContestDefs;
    countyInfo: CountyInfo;
    countyState: County.AppState;
    currentRoundNumber: number;
    history: History;
    startAudit: OnClick;
}

const CountyDashboardPage = (props: PageProps) => {
    const {
        auditBoardSignedIn,
        auditComplete,
        auditStarted,
        canAudit,
        canRenderReport,
        canSignIn,
        contests,
        countyInfo,
        countyState,
        currentRoundNumber,
        history,
        startAudit,
    } = props;

    const startAuditButtonDisabled = !canAudit || auditComplete;
    const auditBoardButtonDisabled = !canSignIn;

    return (
        <div>
            <div className='county-root'>
                <CountyNav />
                <div>
                    <Main auditComplete={ auditComplete }
                          auditStarted={ auditStarted }
                          auditBoardSignedIn={ auditBoardSignedIn }
                          canRenderReport={ canRenderReport }
                          countyState={ countyState }
                          currentRoundNumber={ currentRoundNumber }
                          history={ history }
                          startAuditButtonDisabled={ startAuditButtonDisabled }
                          name={ countyInfo.name }
                          auditBoardButtonDisabled={ auditBoardButtonDisabled }
                          startAudit={ startAudit } />
                    <Info info={ countyInfo }
                          contests={ contests }
                          countyState={ countyState }
                          currentRoundNumber={ currentRoundNumber }/>
                </div>
            </div>
            <LicenseFooter />
        </div>
    );
};


export default CountyDashboardPage;
