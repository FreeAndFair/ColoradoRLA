import * as React from 'react';

import * as _ from 'lodash';

import { Button, Checkbox, Classes, EditableText, MenuItem } from '@blueprintjs/core';
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

type SortKey = 'contest' | 'county';

type SortOrder = 'asc' | 'desc';

class SelectContestsForm extends React.Component<any, any> {
    constructor(props: any) {
        super(props);

        this.state = {
            filter: '',
            form: {},
            order: 'asc',
            sort: 'county',
        };

        _.forEach(props.contests, (c, _) => {
            this.state.form[c.id] = {
                audit: false,
                handCount: false,
                reason: { ...auditReasons[0] },
            };
        });
    }

    public render() {
        const { contests } = this.props;

        this.props.forms.selectContestsForm = this.state.form;

        const contestData = _.map(contests, (c: any) => {
            const props = {
                auditStatus: this.state.form[c.id],
                contest: c,
                key: c.id,
                onAuditChange: this.onAuditChange(c),
                onHandCountChange: this.onHandCountChange(c),
                onReasonChange: this.onReasonChange(c),
            };

            const countyName = counties[c.countyId].name;

            return [
                countyName,
                c.name,
                props,
            ];
        });

        const keyFunc = (d: any[]) => {
            const i = this.state.sort === 'contest' ? 1 : 0;
            return d[i];
        };
        const sortedData = _.sortBy(contestData, keyFunc);

        if (this.state.order === 'desc') {
            _.reverse(sortedData);
        }

        const filterFunc = (d: any[]) => {
            const [countyName, contestName, ...props] = d;

            const str = this.state.filter.toLowerCase();

            return contestName.toLowerCase().includes(str)
                || countyName.toLowerCase().includes(str);

        };
        const filteredData = _.filter(sortedData, filterFunc);

        const contestRows = _.map(filteredData, (d: any[]) => <ContestRow { ...d[2] } />);

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
                <div className='pt-card'>
                    Filter by County or Contest Name:
                    <span> </span>
                    <EditableText
                        className='pt-input'
                        minWidth={ 200 }
                        value={ this.state.filter }
                        onChange={ this.onFilterChange } />
                </div>
                <div className='pt-card'>
                    <table className='pt-table pt-bordered pt-condensed'>
                        <thead>
                            <tr>
                                <th onClick={ this.sortBy('county') }>
                                    County
                                </th>
                                <th onClick={ this.sortBy('contest') }>
                                    Contest Name
                                </th>
                                <th>Audit?</th>
                                <th>Reason</th>
                            </tr>
                        </thead>
                        <tbody>
                            { contestRows }
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }

    private onAuditChange = (contest: any) => () => {
        const s = { ...this.state };

        const { audit } = s.form[contest.id];
        s.form[contest.id].audit = !audit;

        this.setState(s);
    }

    private onFilterChange = (filter: any) => {
        this.setState({ filter });
    }

    private onHandCountChange = (contest: any) => () => {
        const s = { ...this.state };

        const { handCount } = s.form[contest.id];
        s.form[contest.id].handCount = !handCount;

        this.setState(s);
    }

    private onReasonChange = (contest: any) => (item: any) => {
        const s = { ...this.state };

        s.form[contest.id].reason = { ...item };

        this.setState(s);
    }

    private reverseOrder() {
        const order = this.state.order === 'asc'
                    ? 'desc'
                    : 'asc';

        this.setState({ order });
    }

    private sortBy(sort: SortKey) {
        return () => {
            if (this.state.sort === sort) {
                this.reverseOrder();
            } else {
                const order = 'asc';
                this.setState({ sort, order });
            }
        };
    }
}


export default SelectContestsForm;
