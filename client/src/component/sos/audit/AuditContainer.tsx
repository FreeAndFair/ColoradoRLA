import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import AuditPage from './AuditPage';

import setRiskLimit from '../../../action/setRiskLimit';


class AuditContainer extends React.Component<any, any> {
    public render() {
        const { history, setRiskLimit } = this.props;

        const props = {
            nextPage: () => history.push('/sos/audit/seed'),
            setRiskLimit,
        };

        return <AuditPage { ...props } />;
    }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    setRiskLimit,
}, dispatch);

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(AuditContainer);
