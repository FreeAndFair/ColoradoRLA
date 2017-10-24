import * as React from 'react';

import { connect } from 'react-redux';

import StartStage from './StartStage';


interface ContainerProps {
    countyState: County.AppState;
    nextStage: OnClick;
}

class StartStageContainer extends React.Component<ContainerProps> {
    public render() {
        return <StartStage { ...this.props } />;
    }
}

function select(countyState: County.AppState) {
    return { countyState };
}


export default connect(select)(StartStageContainer);
