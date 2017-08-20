import * as React from 'react';
import { connect } from 'react-redux';

import counties from '../../../data/counties';

import CountyHomePage from './CountyHomePage';

import countyFetchCvr from '../../../action/countyFetchCvr';


const intervalIds: any = {
    ballotUnderAuditId: null,
    fetchCvrId: null,
};

class CountyHomeContainer extends React.Component<any, any> {
    public render() {
        const {
            ballotUnderAuditId,
            county,
            history,
        } = this.props;

        if (!intervalIds.fetchCvrId) {
            if (ballotUnderAuditId) {
                intervalIds.ballotUnderAuditId = ballotUnderAuditId;
                countyFetchCvr(ballotUnderAuditId);

                intervalIds.fetchCvrId = setInterval(
                    () => countyFetchCvr(this.props.ballotUnderAuditId),
                    1000,
                );
            }
        } else {
            clearInterval(intervalIds.fetchCvrId);

            if (ballotUnderAuditId &&
                ballotUnderAuditId !== intervalIds.ballotUnderAuditId) {

                intervalIds.ballotUnderAuditId = ballotUnderAuditId;
                countyFetchCvr(ballotUnderAuditId);

                intervalIds.fetchCvrId = setInterval(
                    () => countyFetchCvr(this.props.ballotUnderAuditId),
                    1000,
                );
            }
        }

        const startAudit = () => history.push('/county/audit');

        const countyInfo = county.id ? counties[county.id] : {};

        const props = { countyInfo, startAudit, ...this.props };

        return <CountyHomePage { ...props } />;
    }
}

const mapStateToProps = ({ county }: any) => {
    const { ballotUnderAuditId, contests } = county;

    return { ballotUnderAuditId, contests, county };
};

export default connect(mapStateToProps)(CountyHomeContainer);
