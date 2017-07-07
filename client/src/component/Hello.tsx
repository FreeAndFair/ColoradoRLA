import * as React from "react";


export interface HelloProps {
    greeting: string;
}

export interface HelloState {
    misc: string;
}

export class Hello extends React.Component<HelloProps, HelloState> {
    constructor(props: HelloProps) {
        super(props);

        this.state = { misc: 'I am a string' };
    }

    render() {
        const { greeting } = this.props;
        const { misc } = this.state;

        return (
            <div>
                <h1>{ greeting }</h1>
                <h2>My state: { misc }</h2>
            </div>
        );
    }
}
