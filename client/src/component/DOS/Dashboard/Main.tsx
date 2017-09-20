import * as React from 'react';

import RoundContainer from './RoundContainer';

import fetchReport from 'corla/action/dos/fetchReport';


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
    const { auditDefined, canRenderReport, sos } = props;

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
                <div className='pt-card'>
                    <div>Click to download final audit report.</div>
                    <button
                        className='pt-button'
                        onClick={ fetchReport }>
                        Download
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className='sos-notifications pt-card'>
            { auditDefinition }
            <RoundContainer />
            <div className='pt-card'>
                <div>Click to download intermediate audit report.</div>
                <button
                    className='pt-button'
                    disabled={ !canRenderReport }
                    onClick={ fetchReport }>
                    Download
                </button>
            </div>
        </div>
    );
};


export default Main;
