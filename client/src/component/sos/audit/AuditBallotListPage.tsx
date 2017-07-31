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
            <a className='pt-breadcrumb' href='/sos/admin'>
                Audit Admin
            </a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>
                Ballot List
            </a>
        </li>
    </ul>
);


const Audit = () => {
    const nop = () => ({});

    return (
        <div>
            <Nav />
            <Breadcrumb />
            <h2>Administer an Audit</h2>
            <h3>Audit Definition - Ballot List</h3>
            <div>
                This is the list of ballots to audit generated from the random seed.
                Once this is submitted, it will be released to the counties and the
                previous pages will not be editable.
            </div>
            <div className='pt-card'>
                Ballot List:
                <div>
                    <label className='pt-file-upload'>
                        <input type='file' onChange={ nop } />
                        <span className='pt-file-upload-input'>AcmeCountyBallotManifest.csv</span>
                    </label>
                </div>
            </div>
            <div>
                <button className='pt-button'>Back</button>
                <button className='pt-button pt-intent-primary'>Launch Audit</button>
            </div>
        </div>
    );
};


export default Audit;
