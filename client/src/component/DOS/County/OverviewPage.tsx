import * as React from 'react';

import { Link } from 'react-router-dom';

import * as _ from 'lodash';

import counties from 'corla/data/counties';

import { formatCountyASMState } from 'corla/format';

import Nav from '../Nav';


const Breadcrumb = () => (
    <ul className='pt-breadcrumbs'>
        <li>
            <a className='pt-breadcrumb' href='/sos'>
                SoS
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>
                Counties
            </a>
        </li>
    </ul>
);

interface RowProps {
    county: CountyInfo;
    status: DOS.CountyStatus;
}

const CountyTableRow = (props: RowProps) => {
    const { county, status } = props;

    const countyState = formatCountyASMState(status.asmState);
    const submitted = status.auditedBallotCount;

    const auditedCount = _.get(status, 'discrepancyCount.audited') || '—';
    const unauditedCount = _.get(status, 'discrepancyCount.unaudited') || '—';

    return (
        <tr>
            <td>
                <Link to={ `/sos/county/${county.id}` }>
                    { county.name }
                </Link>
            </td>
            <td>{ countyState }</td>
            <td>{ submitted }</td>
            <td>{ auditedCount }</td>
            <td>{ unauditedCount }</td>
        </tr>
    );
};

interface TableProps {
    countyStatus: DOS.CountyStatuses;
}

const CountyTable = (props: TableProps) => {
    const { countyStatus } = props;

    const countyRows = _.map(counties, c => {
        const status = countyStatus[c.id];

        if (!status) {
            return <div key={ c.id } />;
        }

        return <CountyTableRow key={ c.id } county={ c } status={ status } />;
    });

    return (
        <table className='pt-table pt-bordered pt-condensed'>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Status</th>
                    <th># Ballots Submitted</th>
                    <th>Audited Contest Discrepancies</th>
                    <th>Non-audited Contest Discrepancies</th>
                </tr>
            </thead>
            <tbody>
                { countyRows }
            </tbody>
        </table>
    );
};

interface PageProps {
    countyStatus: DOS.CountyStatuses;
}

const CountyOverviewPage = (props: PageProps) => {
    const { countyStatus } = props;

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <CountyTable countyStatus={ countyStatus } />
        </div>
    );
};


export default CountyOverviewPage;
