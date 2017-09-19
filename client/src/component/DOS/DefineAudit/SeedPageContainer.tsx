import * as React from 'react';
import { connect } from 'react-redux';
import { Redirect } from 'react-router-dom';

import AuditSeedPage from './SeedPage';

import uploadRandomSeed from 'corla/action/dos/uploadRandomSeed';


class AuditSeedContainer extends React.Component<any, any> {
    public render() {
        const { history, publicMeetingDate, seed, sos } = this.props;

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            back: () => history.push('/sos/audit/select-contests'),
            nextPage: () => history.push('/sos/audit/review'),
            publicMeetingDate,
            seed,
            uploadRandomSeed,
        };

        return <AuditSeedPage { ...props } />;
    }
}


const mapStateToProps = ({ sos }: any) => ({
    publicMeetingDate: sos.publicMeetingDate,
    seed: sos.seed,
    sos,
});

export default connect(mapStateToProps)(AuditSeedContainer);
