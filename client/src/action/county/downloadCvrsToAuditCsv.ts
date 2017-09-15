import { endpoint } from 'corla/config';

import createFetchAction from 'corla/action/createFetchAction';


export default (round: number) => {
    const params = `round=${round}&include_audited`;
    const url = `${endpoint('cvr-to-audit-download')}?${params}`;

    window.location.replace(url);
};
