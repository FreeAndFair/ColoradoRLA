import * as _ from 'lodash';


const UPLOADED_STATES = [
    'BALLOT_MANIFEST_OK',
    'BALLOT_MANIFEST_AND_CVRS_OK',
    'COUNTY_AUDIT_UNDERWAY',
    'COUNTY_AUDIT_COMPLETE',
];

function ballotManifestUploaded(state: AppState): boolean {
    if (!state.county) { return false; }
    if (!state.county.asm) { return false; }
    if (!state.county.asm.county) { return false; }

    const { currentState } = state.county.asm.county;

    if (!currentState) { return false; }

    return _.includes(UPLOADED_STATES, currentState);
}


export default ballotManifestUploaded;
