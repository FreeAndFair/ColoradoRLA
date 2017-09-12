import * as React from 'react';

import * as _ from 'lodash';

import { Button, Checkbox, Classes, MenuItem } from '@blueprintjs/core';
import { Select } from '@blueprintjs/labs';

import counties from 'corla/data/counties';


interface FormState {
    contests: any;
    forms: any;
}

const auditReasons = [
    { id: 'state_wide_contest', text: 'State Contest' },
    { id: 'county_wide_contest', text: 'County Contest' },
];

const AuditReasonSelect = Select.ofType<any>();

const ContestRow = (props: any) => {
    const {
        auditStatus,
        contest,
        onAuditChange,
        onHandCountChange,
        onReasonChange,
    } = props;

    if (!auditStatus) {
        return <div />;
    }

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

    const { handCount } = auditStatus;
    const toAudit = !handCount && auditStatus.audit;

    const countyName = counties[contest.countyId].name;

    return (
        <tr>
            <td>{ countyName }</td>
            <td>{ contest.name }</td>
            <td>
                <Checkbox
                    disabled={ handCount }
                    checked={ toAudit }
                    onChange={ onAuditChange } />
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
                handCount: false,
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
                onHandCountChange: this.onHandCountChange(c),
                onReasonChange: this.onReasonChange(c),
            };

            return <ContestRow { ...props } />;
        });

        return (
            <div>
                <div className='pt-card'>
                    According to Colorado statute, at least one statewide contest and
                    one countywide contest must be chosen for audit. The Secretary of State
                    will select other ballot contests for audit if in any particular election
                    there is no statewide contest or a countywide contest in any county. Once
                    these contests for audit have been selected and published, they cannot be
                    changed. The Secretary of State can decide that a contest must witness a
                    full hand count at any time.
                </div>
                <table className='pt-table pt-bordered pt-condensed'>
                    <thead>
                        <tr>
                            <th>County</th>
                            <th>Contest Name</th>
                            <th>Audit?</th>
                            <th>Reason</th>
                        </tr>
                    </thead>
                    <tbody>
                        { contestRows }
                    </tbody>
                </table>
            </div>
        );
    }

    private onAuditChange = (contest: any) => () => {
        const s = { ...this.state };

        const { audit } = s[contest.id];
        s[contest.id].audit = !audit;

        this.setState(s);
    }

    private onHandCountChange = (contest: any) => () => {
        const s = { ...this.state };

        const { handCount } = s[contest.id];
        s[contest.id].handCount = !handCount;

        this.setState(s);
    }

    private onReasonChange = (contest: any) => (item: any) => {
        const s = { ...this.state };

        s[contest.id].reason = { ...item };

        this.setState(s);
    }
}


export default SelectContestsForm;
