import * as React from 'react';
import { connect } from 'react-redux';

import { Redirect } from 'react-router-dom';


interface RootRedirectContainerProps {
    dashboard: Dashboard;
}

export class RootRedirectContainer extends React.Component<RootRedirectContainerProps> {
    public render() {
        const { dashboard } = this.props;

        if (dashboard === 'DOS') {
            return <Redirect to='/sos' />;
        }

        return <Redirect to='/county' />;
    }
}

function isCountyAppState(state: AppState): state is County.AppState {
    return state.type === 'County';
}

function select(state: AppState) {
    const dashboard = isCountyAppState(state) ? 'County' : 'DOS';

    return { dashboard };
}


export default connect(select)(RootRedirectContainer);
