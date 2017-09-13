import { CountyState } from 'corla/asm';

function capitalize(s: string) {
    if (!s) { return ''; }

    const [fst, ...rest] = s.split('');

    return fst.toUpperCase() + rest.join('');
}

export function electionType(type: ElectionType): string {
    return `${capitalize(type)} Election`;
}

export function formatCountyAsmState(state: CountyState) {
    switch (state) {
        case 'COUNTY_INITIAL_STATE':
            return 'Not started';
        case 'COUNTY_AUTHENTICATED':
            return 'Logged in';
        case 'BALLOT_MANIFEST_OK':
            return 'Ballot manifest uploaded';
        case 'CVRS_OK':
            return 'CVR export uploaded';
        case 'BALLOT_MANIFEST_AND_CVRS_OK':
            return 'Ballot manifest and CVR export uploaded';
        case 'COUNTY_AUDIT_UNDERWAY':
            return 'Audit underway';
        case 'COUNTY_AUDIT_COMPLETE':
            return 'Audit complete';
        case 'DEADLINE_MISSED':
            return 'File upload deadline missed';
        default: return '';
    }
}
