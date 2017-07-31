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


const CountyDetailPage = (props: any) => {
    const { county } = props;

    return (
        <div>
            <Nav />
            <Breadcrumb county={ county } />
            <div>
                County details.
            </div>
        </div>
    );
};


export default CountyDetailPage;
