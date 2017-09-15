import * as React from 'react';

import { Intent, Spinner } from '@blueprintjs/core';


const Uploading = () => (
    <div className='pt-card'>
        <Spinner className='pt-large' intent={ Intent.PRIMARY } />
    </div>
);


export default Uploading;
