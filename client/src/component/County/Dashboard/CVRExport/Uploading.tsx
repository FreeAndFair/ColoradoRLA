import * as React from 'react';

import * as _ from 'lodash';

import { Intent, ProgressBar, Spinner } from '@blueprintjs/core';


interface ProgressProps {
    count: number;
    file: UploadedFile;
}

const Progress = (props: ProgressProps) => {
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
                <strong>Import progress:</strong> { progressPercent }%
            </div>
            <ProgressBar className='pt-intent-primary' value={ progressRatio } />
        </div>
    );
};

interface UploadingProps {
    countyState: County.AppState;
}

const Uploading = (props: UploadingProps) => {
    const { countyState } = props;
    const { cvrExportCount, cvrExport, cvrImportStatus } = countyState;

    if (!cvrExportCount) { return null; }
    if (!cvrExport) { return null; }
    if (!cvrImportStatus) { return null; }

    if (cvrImportStatus === 'IN_PROGRESS') {
        return (
            <div className='pt-card'>
                <Progress file={ cvrExport } count={ cvrExportCount } />
            </div>
        );
    } else {
        return (
            <div className='pt-card'>
                <Spinner className='pt-large' intent={ Intent.PRIMARY } />
                <div>Uploading file...</div>
            </div>
        );
    }
};


export default Uploading;
