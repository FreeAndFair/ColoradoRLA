import * as React from 'react';

import downloadFile from 'corla/action/downloadFile';


interface UploadedFileProps {
    description: string;
    file: UploadedFile;
}

const UploadedFile = ({ description, file }: UploadedFileProps) => {
    const onClick = () => downloadFile(file.id);

    return (
        <div className='pt-card'>
            <h4>{ description }</h4>
            <div><strong>File name:</strong> "{ file.name }"</div>
            <div><strong>SHA-256 hash:</strong> { file.hash }</div>
            <button className='pt-button pt-intent-primary' onClick={ onClick }>
                Download
            </button>
        </div>
    );
};

interface DownloadButtonsProps {
    status: County.AppState | DOS.CountyStatus;
}

const FileDownloadButtons = (props: DownloadButtonsProps) => {
    const { status } = props;

    if (!status) {
        return <div />;
    }

    const { ballotManifest, cvrExport } = status;

    if (!ballotManifest || !cvrExport) {
        return <div />;
    }

    return (
        <div className='pt-card'>
            <UploadedFile description='Ballot Manifest' file={ ballotManifest } />
            <UploadedFile description='CVR Export' file={ cvrExport } />
        </div>
    );
};


export default FileDownloadButtons;
