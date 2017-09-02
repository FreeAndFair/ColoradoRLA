import * as _ from 'lodash';


const uploadedStates = [
    'BALLOT_MANIFEST_OK',
    'BALLOT_MANIFEST_AND_CVRS_OK',
    'COUNTY_AUDIT_UNDERWAY',
    'COUNTY_AUDIT_COMPLETE',
];


function ballotManifestUploaded(state: any): boolean {
    if (!_.has(state, 'county.asm.county.currentState')) {
        return false;
    }

    const { currentState } = state.county.asm.county;

    return _.includes(uploadedStates, currentState);
}


export default ballotManifestUploaded;
