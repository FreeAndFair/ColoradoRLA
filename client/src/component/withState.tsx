import * as React from 'react';
import { Redirect } from 'react-router';

import { connect } from 'react-redux';


interface WrapperProps {
    hasState: boolean;
}

function withState(
    stateType: AppStateType,
    Wrapped: React.ComponentClass,
) {
    class Wrapper extends React.Component<WrapperProps> {
        public render() {
            if (this.props.hasState) {
                return <Wrapped />;
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
