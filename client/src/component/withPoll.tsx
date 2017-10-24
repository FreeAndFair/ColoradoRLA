import * as React from 'react';

import { connect } from 'react-redux';

import action from 'corla/action';


function withSync<P, SelectP, BindP, BindS>(
    Wrapped: React.ComponentType<P>,
    didMount: string,
    willUnmount: string,
    select: Select<SelectP>,
    bind?: Bind<BindP, BindS>,

) {
    type WrapperProps = P & SelectP & BindP;

    class Wrapper extends React.Component<WrapperProps> {
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

    if (bind) {
        return connect(select, bind)(Wrapper);
    } else {
        return connect(select)(Wrapper);
    }
}


export default withSync;
