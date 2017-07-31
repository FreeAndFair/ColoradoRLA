import * as React from 'react';

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

const CountyTableRow = ({ county }: any) => {
    return (
        <tr>
            <td>{ county.id }</td>
            <td>{ county.name }</td>
            <td>{ county.started }</td>
            <td>{ county.submitted }</td>
            <td>{ county.discrepancies }</td>
        </tr>
    );
};

const CountyTable = ({ counties }: any) => {
    const countyRows = _.map(counties, (c: any) => (
        <CountyTableRow key={ c.id } county={c} />
    ));

    return (
        <table className='pt-table pt-bordered pt-condensed'>
            <thead>
                <tr>
                    <td>ID</td>
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
    const { counties } = props;

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <CountyTable counties={ counties } />
        </div>
    );
};


export default CountyOverviewPage;
