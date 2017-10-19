import * as React from 'react';

import { connect } from 'react-redux';

import BallotListStage from './BallotListStage';

import countyInfo from 'corla/selector/county/countyInfo';


interface ContainerProps {
    county: CountyState;
    countyInfo: CountyInfo;
    cvrsToAudit: CvrJson[];
    nextStage: OnClick;
}

class BallotListStageContainer extends React.Component<ContainerProps> {
    public render() {
        const props = { ...this.props };

        if (!this.props.cvrsToAudit) {
            return <div />;
        }

        return <BallotListStage { ...props } />;
    }
}

function select(state: AppState) {
    const { county } = state;

    return {
        county,
        countyInfo: countyInfo(state),
        cvrsToAudit: county!.cvrsToAudit,
    };
}


export default connect(select)(BallotListStageContainer);
