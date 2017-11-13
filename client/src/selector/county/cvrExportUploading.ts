import * as _ from 'lodash';


const UPLOADING = [
    'BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING',
    'CVRS_IMPORTING',
];

function cvrExportUploading(state: County.AppState): boolean {
    return _.includes(UPLOADING, state.asm.county);
}


export default cvrExportUploading;
