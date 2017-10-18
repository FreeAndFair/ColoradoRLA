import * as _ from 'lodash';


const UPLOADED_STATES = [
    'CVRS_OK',
    'BALLOT_MANIFEST_AND_CVRS_OK',
    'COUNTY_AUDIT_UNDERWAY',
    'COUNTY_AUDIT_COMPLETE',
];


function cvrExportUploaded(state: AppState): boolean {
    if (!state.county) { return false; }
    if (!state.county.asm) { return false; }
    if (!state.county.asm.county) { return false; }
    if (!state.county.asm.county.currentState) { return false; }

    const { currentState } = state.county.asm.county;

    return _.includes(UPLOADED_STATES, currentState);
}


export default cvrExportUploaded;
