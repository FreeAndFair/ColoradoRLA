import * as React from 'react';

import { connect } from 'react-redux';

import BallotListStage from './BallotListStage';

import countyInfo from 'corla/selector/county/countyInfo';


class BallotListStageContainer extends React.Component<any, any> {
    public render() {
        const props = { ...this.props };

        if (!this.props.cvrsToAudit) {
            return <div />;
        }

        return <BallotListStage { ...props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { county } = state;

    return {
        county,
        countyInfo: countyInfo(state),
        cvrsToAudit: county.cvrsToAudit,
    };
};


export default connect(mapStateToProps)(BallotListStageContainer);
