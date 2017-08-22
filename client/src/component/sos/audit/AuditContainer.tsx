import * as React from 'react';
import { connect } from 'react-redux';

import AuditPage from './AuditPage';

import setRiskLimit from '../../../action/setRiskLimit';


class AuditContainer extends React.Component<any, any> {
    public render() {
        const { history, riskLimit } = this.props;

        const props = {
            nextPage: () => history.push('/sos/audit/select-contests'),
            riskLimit,
            setRiskLimit,
        };

        return <AuditPage { ...props } />;
    }
}


const mapStateToProps = ({ sos }: any) => {
    const { riskLimit } = sos;

    return { riskLimit, sos };
};

export default connect(mapStateToProps)(AuditContainer);
