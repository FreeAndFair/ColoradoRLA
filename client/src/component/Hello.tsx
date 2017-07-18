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

        const buttonClassName = 'pt-button pt-intent-primary';

        return (
            <div className='pt-card'>
                <h1>{ greeting }</h1>
                <h2>My local state is: "{ local }"</h2>
                <button className={buttonClassName} onClick={onClick}>
                    <span className='pt-icon-refresh' /> Toggle
                </button>
            </div>
        );
    }
}
