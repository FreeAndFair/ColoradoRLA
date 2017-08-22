import * as React from 'react';
import { connect } from 'react-redux';

import AuditSeedPage from './AuditSeedPage';

import uploadRandomSeed from '../../../action/uploadRandomSeed';


class AuditSeedContainer extends React.Component<any, any> {
    public render() {
        const { history, seed } = this.props;

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
