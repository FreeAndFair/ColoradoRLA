import * as React from 'react';

import { connect } from 'react-redux';

import BallotListStage from './BallotListStage';


class BallotListStageContainer extends React.Component<any, any> {
    public render() {
        const props = { ...this.props };

        if (!this.props.cvrsToAudit) {
            return <div />;
        }

        return <BallotList { ...props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { county } = state;

    return {
        county,
        cvrsToAudit: county.cvrsToAudit,
    };
};


export default connect(mapStateToProps)(BallotListStageContainer);
