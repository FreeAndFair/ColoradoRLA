import * as React from 'react';


const RiskLimitInfo = ({ riskLimit }: any) => {
    return (
        <div className='pt-card'>
            <strong>Target Risk Limit: </strong> { riskLimit * 100 } %
        </div>
    );
};

const SeedInfo = ({ seed }: any) => {
    return (
        <div className='pt-card'>
            <strong>Seed: </strong> { seed }
        </div>
    );
};

const AuditParams = ({ sos }: any) => {
    return (
        <div>
            <RiskLimitInfo riskLimit={ sos.riskLimit } />
            <SeedInfo seed={ sos.seed } />
        </div>
    );
};

const AuditNotDefined = () => {
    return (
        <div><em>The audit has not yet been defined.</em></div>
    );
};

const Main = (props: any) => {
    const { showAuditParams, sos } = props;

    const auditParams = showAuditParams
                      ? <AuditParams sos={ sos } />
                      : <AuditNotDefined />;

    return (
        <div className='sos-notifications pt-card'>
            { auditParams }
        </div>
    );
};


export default Main;
