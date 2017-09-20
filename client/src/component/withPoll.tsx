import * as React from 'react';

import { connect } from 'react-redux';

import action from 'corla/action';


function withSync(
    Wrapped: any,
    didMount: string,
    willUnmount: string,
    select: (state: any) => any,
    bind?: (dispatch: any) => any,

) {
    class Wrapper extends React.Component<any, any> {
        public componentDidMount() {
            action(didMount);
        }

        public componentWillUnmount() {
            action(willUnmount);
        }

        public render() {
            return <Wrapped { ...this.props } />;
        }
    }

    return connect(select, bind)(Wrapper);
}


export default withSync;
