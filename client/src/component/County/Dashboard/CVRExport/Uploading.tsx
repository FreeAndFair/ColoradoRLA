import * as React from 'react';

import * as _ from 'lodash';

import { Intent, ProgressBar, Spinner } from '@blueprintjs/core';


const Progress = (props: any) => {
    const { count, file } = props;

    if (_.isNil(count)) { return null; }
    if (!file) { return null; }

    const { approximateRecordCount } = file;
    if (_.isNil(approximateRecordCount)) { return null; }

    const progressRatio = count / approximateRecordCount;
    const progressPercent = Math.round(progressRatio * 100);

    return (
        <div className='rla-file-upload-progress'>
            <div>
                <strong>Progress:</strong> { progressPercent }%
            </div>
            <ProgressBar className='pt-intent-primary' value={ progressRatio } />
        </div>
    );

};

const Uploading = (props: any) => {
    const { county } = props;
    const { cvrExportCount, cvrExport, cvrImportStatus } = county;

    const progress = cvrImportStatus === 'IN_PROGRESS'
                   ? <Progress file={ cvrExport } count={ cvrExportCount } />
                   : null;

    return (
        <div className='pt-card'>
            <Spinner className='pt-large' intent={ Intent.PRIMARY } />
            { progress }
        </div>
    );
};


export default Uploading;
