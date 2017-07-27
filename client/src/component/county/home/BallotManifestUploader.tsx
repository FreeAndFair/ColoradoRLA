import * as React from 'react';


const BallotManifestUploader = ({ onChange, fileName }: any) => (
    <div className='pt-card'>
        Ballot Manifest:
        <div>
            <label className='pt-file-upload'>
                <input type='file' onChange={ onChange } />
                <span className='pt-file-upload-input'>{ fileName }</span>
            </label>
        </div>
    </div>
);

export default BallotManifestUploader;
