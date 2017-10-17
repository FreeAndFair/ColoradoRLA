import * as React from 'react';
import { Link } from 'react-router-dom';

import * as _ from 'lodash';

import CountyNav from '../Nav';

import * as config from 'corla/config';


const MissedDeadlinePage = () => {
    return (
        <div className='county-root'>
            <CountyNav />
            <h2>Upload Deadline Missed</h2>
            <div>
                <div className='pt-card'>
                    You are unable to upload a file because the deadline has passed and the
                    audit has begun. Please contact the CDOS voting systems team at
                    <span>{ config.helpEmail }</span> or { config.helpTel } for assistance.
                </div>
            </div>
        </div>
    );
};


export default MissedDeadlinePage;
