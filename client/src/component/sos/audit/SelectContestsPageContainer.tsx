import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';

import SelectContestsPage from './SelectContestsPage';

import selectContestsForAudit from '../../../action/selectContestsForAudit';


class SelectContestsPageContainer extends React.Component<any, any> {
    public render() {
        const { history, contests, selectContestsForAudit } = this.props;

        const props = {
            back: () => history.push('/sos/audit'),
            contests,
            nextPage: () => history.push('/sos/audit/review'),
            selectContestsForAudit,
        };

        return <SelectContestsPage { ...props } />;
    }
}

const mapStateToProps = ({ sos }: any) => ({
    contests: sos.contests,
    sos,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => bindActionCreators({
    selectContestsForAudit,
}, dispatch);


export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(SelectContestsPageContainer);
