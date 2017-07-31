import * as React from 'react';

import Nav from '../Nav';


const Breadcrumb = ({ contest }: any) => (
    <ul className='pt-breadcrumbs'>
        <li>
            <a className='pt-breadcrumb pt-disabled' href='/sos'>
                SoS
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb' href='/sos/contest'>
                Contest
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>
                { contest.name }
            </a>
        </li>
    </ul>
);


const ContestDetailPage = (props: any) => {
    const { contest } = props;

    return (
        <div>
            <Nav />
            <Breadcrumb contest={ contest } />
            <h2>Status</h2>
            <h3>County Data</h3>
            <div>Ballot manifest: uploaded</div>
            <div>CVR export: uploaded</div>
            <div>Projected start date: 11/15/2017</div>
            <div>
                <h3>Progress</h3>
                <div>
                    Audit board:
                    <div>John Doe (Democratic Party)</div>
                    <div>Jane Smith (RepublicanParty)</div>
                </div>
                <div>
                    <div>Round 1</div>
                    <div>Start time: 11/15/2016, 11:00 a.m. MST</div>
                    <div>Ballots submitted: 1001</div>
                    <div>Discrepancies: 3</div>
                </div>

            </div>
        </div>
    );
};


export default ContestDetailPage;
