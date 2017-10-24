import * as _ from 'lodash';


const UPLOADED_STATES = [
    'CVRS_OK',
    'BALLOT_MANIFEST_AND_CVRS_OK',
    'COUNTY_AUDIT_UNDERWAY',
    'COUNTY_AUDIT_COMPLETE',
];

function cvrExportUploaded(state: County.AppState): boolean {
    return _.includes(UPLOADED_STATES, state.asm.county);
}


export default cvrExportUploaded;
