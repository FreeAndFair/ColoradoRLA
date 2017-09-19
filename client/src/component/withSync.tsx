import * as React from 'react';

import { connect } from 'react-redux';

import action from 'corla/action';


function withPoll(
    Wrapped: any,
    didMount: string,
    select: (state: any) => any,
    bind?: (dispatch: any) => any,

) {
    class Wrapper extends React.Component<any, any> {
        public componentDidMount() {
            action(didMount);
        }

        public render() {
            return <Wrapped { ...this.props } />;
        }
    }

    return connect(select, bind)(Wrapper);
}


export default withPoll;
