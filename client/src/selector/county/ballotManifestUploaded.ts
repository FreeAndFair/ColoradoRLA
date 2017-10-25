import * as _ from 'lodash';


const UPLOADED_STATES = [
    'BALLOT_MANIFEST_OK',
    'BALLOT_MANIFEST_OK_AND_CVRS_IMPORTING',
    'BALLOT_MANIFEST_AND_CVRS_OK',
    'COUNTY_AUDIT_UNDERWAY',
    'COUNTY_AUDIT_COMPLETE',
];

function ballotManifestUploaded(state: County.AppState): boolean {
    return _.includes(UPLOADED_STATES, state.asm.county);
}


export default ballotManifestUploaded;
