import * as React from 'react';
import { connect } from 'react-redux';

import AuditSeedPage from './AuditSeedPage';


class AuditSeedContainer extends React.Component<any, any> {
    public render() {
        return <AuditSeedPage />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: any) => ({});

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(AuditSeedContainer);
