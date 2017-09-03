import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import AuditSeedPage from './SeedPage';

import uploadRandomSeed from 'corla/action/uploadRandomSeed';


class AuditSeedContainer extends React.Component<any, any> {
    public render() {
        const { history, seed, sos } = this.props;

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            back: () => history.push('/sos/audit/select-contests'),
            nextPage: () => history.push('/sos/audit/review'),
            seed,
            uploadRandomSeed,
        };

        return <AuditSeedPage { ...props } />;
    }
}


const mapStateToProps = ({ sos }: any) => ({ sos, seed: sos.seed });

export default connect(mapStateToProps)(AuditSeedContainer);
