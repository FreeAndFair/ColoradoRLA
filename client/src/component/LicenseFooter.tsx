import * as React from 'react';


const projectUrl = 'https://github.com/FreeAndFair/ColoradoRLA';

const License = () => {
    return (
        <div className='pt-card rla-license'>
            The <em>Colorado RLA Tool</em> is Copyright (C) 2017 the Colorado Department of
            State, and is licensed under the AGPLv3 with a classpath exception.
            See the project's <a href={ projectUrl }>GitHub site</a> for more information.
        </div>
    );
};

const LicenseFooter = () => {
    return (
        <div className='rla-license-footer' >
            <License />
        </div>
    );
};


export default LicenseFooter;
