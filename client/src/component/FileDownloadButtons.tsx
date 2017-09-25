import * as React from 'react';

import downloadFile from 'corla/action/downloadFile';


const UploadedFile = ({ description, file }: any) => {
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

const FileDownloadButtons = (props: any) => {
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
