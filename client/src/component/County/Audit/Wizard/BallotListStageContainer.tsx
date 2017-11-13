import * as React from 'react';

import { connect } from 'react-redux';

import BallotListStage from './BallotListStage';

import countyInfo from 'corla/selector/county/countyInfo';


interface ContainerProps {
    countyInfo: CountyInfo;
    countyState: County.AppState;
    cvrsToAudit: JSON.CVR[];
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

function select(countyState: County.AppState) {
    return {
        countyInfo: countyInfo(countyState),
        countyState,
        cvrsToAudit: countyState.cvrsToAudit,
    };
}


export default connect(select)(BallotListStageContainer);
