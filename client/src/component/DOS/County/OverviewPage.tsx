import * as React from 'react';

import { Link } from 'react-router-dom';

import * as _ from 'lodash';

import { formatCountyAsmState } from 'corla/format';

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

const CountyTableRow = ({ county, status }: any) => {
    const countyState = formatCountyAsmState(status.asmState);
    const submitted = status.auditedBallotCount;

    const auditedCount = _.get(status, 'discrepancyCount.audited') || '—';
    const oppCount = _.get(status, 'discrepancyCount.opportunistic') || '—';

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
            <td>{ oppCount }</td>
        </tr>
    );
};

const CountyTable = ({ counties, countyStatus }: any) => {
    const countyRows: any = _.map(counties, (c: any) => {
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

const CountyOverviewPage = (props: any) => {
    const { counties, countyStatus } = props;

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <CountyTable counties={ counties } countyStatus={ countyStatus }/>
        </div>
    );
};


export default CountyOverviewPage;
