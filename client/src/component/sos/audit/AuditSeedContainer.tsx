import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import AuditSeedPage from './AuditSeedPage';

import uploadRandomSeed from '../../../action/uploadRandomSeed';


class AuditSeedContainer extends React.Component<any, any> {
    public render() {
        const { history, seed, uploadRandomSeed } = this.props;

        const props = {
            back: () => history.push('/sos/audit'),
            nextPage: () => history.push('/sos/audit/select-contests'),
            seed,
            uploadRandomSeed,
        };

        return <AuditSeedPage { ...props } />;
    }
}

const mapStateToProps = ({ sos }: any) => ({ sos, seed: sos.seed });

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    uploadRandomSeed,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(AuditSeedContainer);
