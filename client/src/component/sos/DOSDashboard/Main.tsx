import * as React from 'react';

import RoundContainer from './RoundContainer';


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

const Definition = ({ sos }: any) => {
    return (
        <div>
            <RiskLimitInfo riskLimit={ sos.riskLimit } />
            <SeedInfo seed={ sos.seed } />
        </div>
    );
};

const NotDefined = () => {
    return (
        <div><em>The audit has not yet been defined.</em></div>
    );
};

const Main = (props: any) => {
    const { auditDefined, sos } = props;

    const auditDefinition = auditDefined
                          ? <Definition sos={ sos } />
                          : <NotDefined />;

    if (sos.asm.currentState === 'DOS_AUDIT_COMPLETE') {
        return (
            <div className='sos-notifications pt-card'>
                { auditDefinition }
                <div className='pt-card'>
                    Congratulations! The audit is complete.
                </div>
            </div>
        );
    }

    return (
        <div className='sos-notifications pt-card'>
            { auditDefinition }
            <RoundContainer />
        </div>
    );
};


export default Main;
