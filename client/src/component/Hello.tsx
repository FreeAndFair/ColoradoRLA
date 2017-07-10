import * as React from 'react';


export interface HelloProps {
    greeting: string;
}

export interface HelloState {
    misc: string;
}

// FIX: We shouldn't have to weaken the Props type param to `any`.
export class Hello extends React.Component<HelloProps & any, HelloState> {
    constructor(props: any) {
        super(props);

        this.state = { misc: 'Some state' };
    }

    render() {
        const { path } = this.props.match;

        return (
            <div>
                <h1>{ path }</h1>
            </div>
        );
    }
}
