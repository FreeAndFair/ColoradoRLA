import * as React from 'react';

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
                        <td>{ status.discrepancyCount.audited }</td>
                    </tr>
                    <tr>
                        <td>Non-audited Contest Discrepancies:</td>
                        <td>{ status.discrepancyCount.opportunistic }</td>
                    </tr>
                </tbody>
            </table>
            <div className='pt-card'>Audit board:
                <ul>
                    <li>John Doe (Democratic Party)</li>
                    <li>Jane Smith (RepublicanParty)</li>
                </ul>
            </div>
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
