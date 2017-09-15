import * as React from 'react';

import * as _ from 'lodash';

import Nav from '../Nav';


const Breadcrumb = ({ county }: any) => (
    <ul className='pt-breadcrumbs'>
        <li>
            <a className='pt-breadcrumb pt-disabled' href='/sos'>
                SoS
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb' href='/sos/county'>
                County
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>
                { county.name }
            </a>
        </li>
    </ul>
);

const CountyDetails = ({ county, status }: any) => {
    const started = status !== 'NO_DATA' ? '✔' : '';
    const submitted = status.auditedBallotCount;

    const auditedCount = _.get(status, 'discrepancyCount.audited') || '—';
    const oppCount = _.get(status, 'discrepancyCount.opportunistic') || '—';

    return (
        <div className='pt-card'>
            <table className='pt-table pt-bordered pt-condensed'>
                <tbody>
                    <tr>
                        <td>Name:</td>
                        <td>{ county.name }</td>
                    </tr>
                    <tr>
                        <td>Started:</td>
                        <td>{ started }</td>
                    </tr>
                    <tr>
                        <td>Ballots Submitted:</td>
                        <td>{ submitted }</td>
                    </tr>
                    <tr>
                        <td>Audited Contest Discrepancies:</td>
                        <td>{ auditedCount }</td>
                    </tr>
                    <tr>
                        <td>Non-audited Contest Discrepancies:</td>
                        <td>{ oppCount }</td>
                    </tr>
                </tbody>
            </table>
        </div>
    );
};


const CountyDetailPage = ({ county, status }: any) => {
    return (
        <div>
            <Nav />
            <Breadcrumb county={ county } />
            <h3>{ county.name }</h3>
            <CountyDetails county={ county } status={ status } />
        </div>
    );
};


export default CountyDetailPage;
