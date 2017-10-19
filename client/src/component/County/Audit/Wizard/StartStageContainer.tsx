import * as React from 'react';

import { connect } from 'react-redux';

import StartStage from './StartStage';


interface ContainerProps {
    county: County.AppState;
    nextStage: OnClick;
}

class StartStageContainer extends React.Component<ContainerProps> {
    public render() {
        return <StartStage { ...this.props } />;
    }
}

function select(state: AppState) {
    const { county } = state;

    return { county };
}


export default connect(select)(StartStageContainer);
