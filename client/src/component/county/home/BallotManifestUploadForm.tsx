import * as React from 'react';

import { EditableText } from '@blueprintjs/core';


class BallotManifestUploadForm extends React.Component<any, any> {
    public state: any = {
        file: null,
        hash: '',
    };

    public render() {
        const { disableReupload, fileUploaded, forms, upload } = this.props;
        const { file, hash } = this.state;

        forms.ballotManifestForm = this.state;

        const fileName = file ? file.name : '';

        const cancelButton = (
            <button className='pt-button pt-intent-warning' onClick={ disableReupload }>
                Cancel
            </button>
        );

        const renderedCancelButton = fileUploaded
                                   ? cancelButton
                                   : '';

        return (
            <div className='pt-card'>
                <div className='pt-card'>
                    <div>
                        Ballot Manifest file
                    </div>
                    <label className='pt-file-upload truncate'>
                        <input type='file' onChange={ this.onFileChange } />
                        <span className='pt-file-upload-input'>{ fileName }</span>
                    </label>
                </div>
                <div className='pt-card'>
                    <div>
                        SHA-256 hash for Ballot Manifest file
                    </div>
                    <label>
                        <EditableText
                            className='pt-input'
                            minWidth={ 500 }
                            maxLength={ 64 }
                            value={ hash }
                            onChange={ this.onHashChange } />
                    </label>
                </div>
                { renderedCancelButton }
                <button className='pt-button' onClick={ upload }>
                    Upload
                </button>
            </div>
        );
    }

    public onFileChange = (e: any) => {
        const s = { ...this.state };

        s.file = e.target.files[0];

        this.setState(s);
    }

    public onHashChange = (hash: any) => {
        const s = { ...this.state };

        s.hash = hash;

        this.setState(s);
    }
}


export default BallotManifestUploadForm;
