import * as React from 'react';

import * as _ from 'lodash';

import { Tooltip } from '@blueprintjs/core';

import SoSNav from '../Nav';

import LicenseFooter from 'corla/component/LicenseFooter';

import ContestUpdates from './ContestUpdates';
import CountyUpdates from './CountyUpdates';
import MainContainer from './MainContainer';


interface PageProps {
    auditStarted: boolean;
    contests: DOS.Contests;
    countyStatus: DOS.CountyStatuses;
    dosState: DOS.AppState;
    seed: string;
}

const DOSDashboardPage = (props: PageProps) => {
    const { auditStarted, contests, countyStatus, dosState, seed } = props;

    return (
        <div>
            <div className='sos-home'>
                <SoSNav />
                <MainContainer />
                <div className='sos-info pt-card'>
                    <CountyUpdates auditStarted={ auditStarted }
                                   countyStatus={ countyStatus } />
                    <ContestUpdates contests={ contests }
                                    seed={ seed }
                                    dosState={ dosState } />
                </div>
            </div>
            <LicenseFooter />
        </div>
    );
};


export default DOSDashboardPage;
