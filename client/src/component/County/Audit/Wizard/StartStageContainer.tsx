import * as React from 'react';

import { connect } from 'react-redux';

import StartStage from './StartStage';


class StartStageContainer extends React.Component<any, any> {
    public render() {
        return <StartStage { ...this.props } />;
    }
}

const mapStateToProps = (state: any) => {
    const { county } = state;

    return { county };
};


export default connect(mapStateToProps)(StartStageContainer);
