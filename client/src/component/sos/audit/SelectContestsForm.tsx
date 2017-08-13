import * as React from 'react';

import * as _ from 'lodash';

import { Button, Checkbox, Classes, MenuItem } from '@blueprintjs/core';
import { Select } from '@blueprintjs/labs';


interface FormState {
    contests: any;
    forms: any;
}

const auditReasons = [
    { id: 'no_reason', text: 'No reason' },
    { id: 'state_wide_contest', text: 'State-wide contest' },
    { id: 'county_wide_contest', text: 'County-wide contest' },
    { id: 'close_contest', text: 'Close contest' },
    { id: 'geographical_scope', text: 'Geographical scope' },
    { id: 'concern_regarding_accuracy', text: 'Concern regarding accuracy' },
    { id: 'opportunistic_benefits', text: 'Opportunistic benefits' },
    { id: 'county_clerk_ability', text: 'County clerk ability' },
    { id: 'no_audit', text: 'No audit' },
];

const AuditReasonSelect = Select.ofType<any>();

const ContestRow = (props: any) => {
    const { auditStatus, contest, onAuditChange, onReasonChange } = props;

    const renderItem = ({ handleClick, item, isActive }: any) => {
        return (
            <MenuItem
                className={ isActive ? Classes.ACTIVE : '' }
                key={ item.id }
                onClick={ handleClick }
                text={ item.text } />
        );
    };

    const popoverClassName = Classes.MINIMAL;

    const auditReasonSelect = (
        <AuditReasonSelect
            filterable={ false }
            key={ contest.id }
            items={ auditReasons }
            itemRenderer={ renderItem }
            onItemSelect={ onReasonChange }
            popoverProps={ { popoverClassName } }>
            <Button
                text={ auditStatus.reason.text }
                rightIconName='double-caret-vertical' />
        </AuditReasonSelect>
    );

    return (
        <tr>
            <td>{ contest.id }</td>
            <td>{ contest.name }</td>
            <td>
                <Checkbox checked={ auditStatus.audit } onChange={ onAuditChange } />
            </td>
            <td>
                { auditStatus.audit ? auditReasonSelect : '' }
            </td>
        </tr>
        );
};

class SelectContestsForm extends React.Component<any, any> {
    constructor(props: any) {
        super(props);

        this.state = {};

        _.forEach(props.contests, (c, _) => {
            this.state[c.id] = {
                audit: false,
                reason: { ...auditReasons[0] },
            };
        });
    }

    public render() {
        const { contests } = this.props;

        this.props.forms.selectContestsForm = this.state;

        const contestRows = _.map(contests, (c: any) => {
            const props = {
                auditStatus: this.state[c.id],
                contest: c,
                key: c.id,
                onAuditChange: this.onAuditChange(c),
                onReasonChange: this.onReasonChange(c),
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

    private onAuditChange = (contest: any) => () => {
        const s = { ...this.state };

        const { audit } = s[contest.id];
        s[contest.id].audit = !audit;

        this.setState(s);
    }

    private onReasonChange = (contest: any) => (item: any) => {
        const s = { ...this.state };

        s[contest.id].reason = { ...item };

        this.setState(s);
    }
}


export default SelectContestsForm;
