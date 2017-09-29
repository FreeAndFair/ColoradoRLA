import * as React from 'react';

import * as _ from 'lodash';

import { Tooltip } from '@blueprintjs/core';

import SoSNav from '../Nav';

import LicenseFooter from 'corla/component/LicenseFooter';

import ContestUpdates from './ContestUpdates';
import CountyUpdates from './CountyUpdates';
import MainContainer from './MainContainer';


const DOSDashboardPage = (props: any) => {
    const { contests, countyStatus, seed, sos } = props;

    return (
        <div>
            <div className='sos-home'>
                <SoSNav />
                <MainContainer />
                <div className='sos-info pt-card'>
                    <CountyUpdates countyStatus={ countyStatus } />
                    <ContestUpdates contests={ contests } seed={ seed } sos={ sos } />
                </div>
            </div>
            <LicenseFooter />
        </div>
    );
};


export default DOSDashboardPage;
