import { endpoint } from 'corla/config';


export default (round: number) => {
    const params = `round=${round}&include_audited`;
    const url = `${endpoint('cvr-to-audit-download')}?${params}`;

    window.location.replace(url);
};
