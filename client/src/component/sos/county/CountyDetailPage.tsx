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

const CountyDetails = ({ county }: any) => {
    return (
        <div className='pt-card'>
            <table className='pt-table pt-bordered pt-condensed'>
                <tbody>
                    <tr>
                        <td>ID:</td>
                        <td>{ county.id }</td>
                    </tr>
                    <tr>
                        <td>Name:</td>
                        <td>{ county.name }</td>
                    </tr>
                    <tr>
                        <td>Started:</td>
                        <td>{ county.started }</td>
                    </tr>
                    <tr>
                        <td>Ballots Submitted:</td>
                        <td>{ county.submitted }</td>
                    </tr>
                    <tr>
                        <td>Discrepancies:</td>
                        <td>{ county.discrepancies }</td>
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


const CountyDetailPage = (props: any) => {
    const { county } = props;

    return (
        <div>
            <Nav />
            <Breadcrumb county={ county } />
            <h3>{ county.name }</h3>
            <CountyDetails county={ county } />
        </div>
    );
};


export default CountyDetailPage;
