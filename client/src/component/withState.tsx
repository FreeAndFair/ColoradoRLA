import * as React from 'react';
import { Redirect } from 'react-router';

import { connect } from 'react-redux';


interface WrapperProps {
    hasState: boolean;
}

function withState<P>(
    stateType: AppStateType,
    Wrapped: React.ComponentType<P>,
) {
    class Wrapper extends React.Component<WrapperProps> {
        public render() {
            const { hasState, ...rest } = this.props;

            if (hasState) {
                return <Wrapped { ...rest } />;
            }

            return <Redirect to='/login' />;
        }
    }

    function select(state: AppState) {
        const hasState = state.type === stateType;

        return { hasState };
    }

    return connect(select)(Wrapper);
}


export default withState;
