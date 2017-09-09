import * as React from 'react';

import { Link } from 'react-router-dom';

import * as _ from 'lodash';

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
    const started = status !== 'NO_DATA' ? '✔' : '';
    const submitted = '';
    const discrepancies = '';

    return (
        <tr>
            <td>
                <Link to={ `/sos/county/${county.id}` }>
                    { county.name }
                </Link>
            </td>
            <td>{ started }</td>
            <td>{ submitted }</td>
            <td>{ discrepancies }</td>
        </tr>
    );
};

const CountyTable = ({ counties, countyStatus }: any) => {
    const countyRows = _.map(counties, (c: any) => {
        const status = countyStatus[c.id];

        return <CountyTableRow key={ c.id } county={ c } status={ status } />;
    });

    return (
        <table className='pt-table pt-bordered pt-condensed'>
            <thead>
                <tr>
                    <td>Name</td>
                    <td>Audit Started</td>
                    <td># Ballots Submitted</td>
                    <td># Discrepancies</td>
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
