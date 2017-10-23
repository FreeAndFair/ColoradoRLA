import * as React from 'react';
import { connect } from 'react-redux';

import { Redirect } from 'react-router-dom';

import { isCountyAppState } from 'corla/type';


interface RootRedirectContainerProps {
    stateType: AppStateType;
}

export class RootRedirectContainer extends React.Component<RootRedirectContainerProps> {
    public render() {
        const { stateType } = this.props;

        if (stateType === 'County') {
            return <Redirect to='/county' />;
        }

        if (stateType === 'DOS') {
            return <Redirect to='/sos' />;
        }

        return <Redirect to='/login' />;
    }
}

function select(state: AppState) {
    return { stateType: state.type };
}


export default connect(select)(RootRedirectContainer);
