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
            <div>
                Contest details.
            </div>
        </div>
    );
};


export default ContestDetailPage;
