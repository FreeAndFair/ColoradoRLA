import * as React from 'react';

import * as _ from 'lodash';

import { Checkbox } from '@blueprintjs/core';


interface FormState {
    contests: any;
}

const ContestRow = (props: any) => {
    const { contest, onRowChange } = props;

    return (
        <tr>
            <td>{ contest.id }</td>
            <td>{ contest.name }</td>
            <td>
                <Checkbox checked={ contest.audit } onChange={ onRowChange } />
            </td>
            <td>...</td>
        </tr>
    );
};

class SelectContestsForm extends React.Component<any, any> {
    constructor(props: any) {
        super(props);

        this.state = {};

        _.forEach(props.contests, (_, id) => {
            this.state[id] = false;
        });
    }

    public render() {
        const { contests } = this.props;

        const contestRows = _.map(contests, (c: any) => {
            const props = {
                contest: c,
                key: c.id,
                onRowChange: this.onRowChange(c),
            };

            return <ContestRow { ...props } />;
        });

        return (
            <table className='pt-table pt-bordered pt-condensed'>
                <thead>
                    <tr>
                        <th>Contest ID</th>
                        <th>Contest Name</th>
                        <th>Audit?</th>
                        <th>Reason</th>
                    </tr>
                </thead>
                <tbody>
                    { contestRows }
                </tbody>
            </table>
        );
    }

    private onRowChange = (contest: any) => () => {
        const s = { ...this.state };

        s[contest.id] = !s[contest.id];

        this.setState(s);
    }
}


export default SelectContestsForm;
