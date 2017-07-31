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
                Audit Admin
            </a>
        </li>
    </ul>
);


const Audit = () => {
    return (
        <div>
            <Nav />
            <Breadcrumb />
            <h2>Audit Admin</h2>
        </div>
    );
};


export default Audit;
