import * as React from 'react';

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

const CountyOverviewPage = (props: any) => {
    return (
        <div>
            <Nav />
            <Breadcrumb />
            <div>
                County overview.
            </div>
        </div>
    );
};


export default CountyOverviewPage;
