import * as React from 'react';
import { Link } from 'react-router-dom';

import * as _ from 'lodash';

import CountyNav from '../Nav';

import BallotManifestUploaderContainer from './BallotManifestUploaderContainer';
import CVRUploaderContainer from './CVRUploaderContainer';


const Main = ({ buttonEnabled, name, startAudit }: any) => (
    <div className='county-main pt-card'>
        <h1>Hello, { name }!</h1>
        <div>
            <div>
                Please upload your Ballot Manifest and Cast Vote Records.
            </div>
            <BallotManifestUploaderContainer />
            <CVRUploaderContainer />
            <button disabled={ false } className='pt-button pt-intent-primary' onClick={ startAudit }>
                Start Audit
            </button>
        </div>
    </div>
);

const ContestInfoTableRow = ({ choice }: any) => (
    <tr>
        <td>{ choice.name }</td>
        <td>{ choice.description }</td>
    </tr>
);

const ContestInfoTable = ({ contest }: any) => {
    const body = _.map(contest.choices, (c: any) => {
        return <ContestInfoTableRow key={ c.name } choice={ c } />;
    });

    return (
        <div className='pt-card'>
            <span>{ contest.name }</span>
            <table className='pt-table'>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                    </tr>
                </thead>
                <tbody>
                    { body }
                </tbody>
            </table>
        </div>
    );
};

const ContestInfo = ({ contests }: any): any => {
    const contestTables = _.map(contests, (c: any) => {
        if (!c) {
            return <div />;
        }

        return <ContestInfoTable key={ c.name } contest={ c } />;
    });

    return (
        <div className='contest-info pt-card'>
            <h3>Contest info</h3>
            <div>
                { contestTables }
            </div>
        </div>
    );
};

const CountyInfo = ({ info }: any) => (
    <div className='county-info pt-card'>
        <h3>County Info</h3>
        <div>
            Election Date: { info.electionDate }
        </div>
        <div>
            Audit Date: { info.auditDate }
        </div>
    </div>
);

const Info = ({ info, contests }: any) => (
    <div className='info pt-card'>
        <CountyInfo info={ info } />
        <ContestInfo contests={ contests } />
    </div>
);

const CountyHomePage = (props: any) => {
    const {
        contests,
        county,
        countyDashboardRefresh,
        startAudit,
    } = props;
    const { ballots, name, startTimestamp, status } = county;

    const info = { auditDate: startTimestamp };

    return (
        <div className='county-root'>
            <CountyNav />
            <div>
                <Main name={ name } startAudit={ startAudit } />
                <Info info={ info } contests={ contests } />
            </div>
        </div>
    );
};

export default CountyHomePage;
