import * as React from 'react';


export interface HelloProps {
    greeting: string;
    onClick: any;
}

export interface HelloState {
    local: string;
}

export class Hello extends React.Component<HelloProps, HelloState> {
    constructor(props: any) {
        super(props);

        this.state = { local: 'Some state' };
    }

    public render() {
        const { greeting, onClick } = this.props;
        const { local } = this.state;

        return (
            <div onClick={onClick} >
                <h1>{ greeting }</h1>
                <h2>My local state is: "{ local }"</h2>
            </div>
        );
    }
}
