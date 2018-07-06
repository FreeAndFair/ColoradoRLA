import * as React from 'react';

import * as _ from 'lodash';

import { Button, Checkbox, Classes, EditableText, MenuItem } from '@blueprintjs/core';
import { Select } from '@blueprintjs/labs';

import counties from 'corla/data/counties';


const auditReasons: DOS.Form.SelectContests.Reason[] = [
    { id: 'state_wide_contest', text: 'State Contest' },
    { id: 'county_wide_contest', text: 'County Contest' },
];

const AuditReasonSelect = Select.ofType<DOS.Form.SelectContests.Reason>();

interface RowProps {
    contest: Contest;
    onAuditChange: OnClick;
    onHandCountChange: OnClick;
    onReasonChange: OnClick;
    status: DOS.Form.SelectContests.ContestStatus;
}

const TiedContestRow = (props: RowProps) => {
    const { contest } = props;

    const countyName = counties[contest.countyId].name;

    return (
        <tr>
            <td>{ countyName }</td>
            <td>{ contest.name }</td>
            <td>
                <Checkbox checked={ false }
                          disabled={ true } />
            </td>
            <td>
                <em>Contest cannot be audited due to a reported tie.</em>
            </td>
        </tr>
    );
};

interface MenuItemData {
    handleClick: OnClick;
    item: DOS.Form.SelectContests.Reason;
    isActive: boolean;
}

const ContestRow = (props: RowProps) => {
    const {
        status,
        contest,
        onAuditChange,
        onHandCountChange,
        onReasonChange,
    } = props;

    if (!status) {
        return null;
    }

    const renderItem = ({ handleClick, item, isActive }: MenuItemData) => {
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
                text={ status.reason.text }
                rightIconName='double-caret-vertical' />
        </AuditReasonSelect>
    );

    const { handCount } = status;
    const toAudit = !handCount && status.audit;

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
                { status.audit ? auditReasonSelect : '' }
            </td>
        </tr>
    );
};

type SortKey = 'contest' | 'county';

type SortOrder = 'asc' | 'desc';

interface FormProps {
    contests: DOS.Contests;
    auditedContests: DOS.AuditedContests;
    forms: DOS.Form.SelectContests.Ref;
    isAuditable: OnClick;
}

interface FormState {
    filter: string;
    form: DOS.Form.SelectContests.FormData;
    order: SortOrder;
    sort: SortKey;
}

class SelectContestsForm extends React.Component<FormProps, FormState> {
    constructor(props: FormProps) {
        super(props);
        const {auditedContests} = props;
        const auditedContestIds = _.map(auditedContests, ac => {return ac.id});

        this.state = {
            filter: '',
            form: {},
            order: 'asc',
            sort: 'county',
        };

        _.forEach(props.contests, (c, _) => {
            const auditable = props.isAuditable(c.id);

            if (auditable) {
                this.state.form[c.id] = {
                    // by using the dosState we can fix mistakes to selected contests
                    audit: auditedContestIds.includes(c.id),
                    handCount: false,
                    reason: { ...auditReasons[0] },
                };
            }
        });
    }

    public componentWillReceiveProps(nextProps: FormProps) {
        if (!_.isEqual(nextProps.contests, this.props.contests)) {
            this.resetForm(nextProps.contests);
        }
    }

    public render() {
        const { contests, isAuditable } = this.props;

        this.props.forms.selectContestsForm = this.state.form;

        type ContestData = [string, string, RowProps];

        const contestData: ContestData[] = _.map(contests, (c): ContestData => {
            const props = {
                contest: c,
                key: c.id,
                onAuditChange: this.onAuditChange(c),
                onHandCountChange: this.onHandCountChange(c),
                onReasonChange: this.onReasonChange(c),
                status: this.state.form[c.id],
            };

            const countyName = counties[c.countyId].name;

            return [
                countyName,
                c.name,
                props,
            ];
        });

        const keyFunc = (d: ContestData) => {
            const i = this.state.sort === 'contest' ? 1 : 0;
            return d[i];
        };
        const sortedData = _.sortBy(contestData, keyFunc);

        if (this.state.order === 'desc') {
            _.reverse(sortedData);
        }

        const filterFunc = (d: ContestData) => {
            const [countyName, contestName, ...props] = d;

            const str = this.state.filter.toLowerCase();

            return contestName.toLowerCase().includes(str)
                || countyName.toLowerCase().includes(str);

        };
        const filteredData = _.filter(sortedData, filterFunc);

        const contestRows = _.map(filteredData, (d: ContestData) => {
            const props = d[2];
            const { contest } = props;

            const auditable = isAuditable(contest.id);

            if (auditable) {
                return <ContestRow { ...props } />;
            } else {
                return <TiedContestRow { ...props } />;
            }
        });

        const sortAscIcon = <span className='pt-icon-standard pt-icon-sort-asc' />;
        const sortDescIcon = <span className='pt-icon-standard pt-icon-sort-desc' />;

        const sortIconForCol = (col: string) => {
            if (col !== this.state.sort) {
                return null;
            }

            return this.state.order === 'asc'
                 ? sortAscIcon
                 : sortDescIcon;
        };

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
                <div className='pt-card' >
                    Click on the "County" or "Contest" column name to sort by that
                    column's data. To reverse sort, click on the column name again.
                </div>
                <div className='pt-card'>
                    <table className='pt-table pt-bordered pt-condensed'>
                        <thead>
                            <tr>
                                <th onClick={ this.sortBy('county') }>
                                    County
                                    <span> </span>
                                    { sortIconForCol('county') }
                                </th>
                                <th onClick={ this.sortBy('contest') }>
                                    Contest Name
                                    <span> </span>
                                    { sortIconForCol('contest') }
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

    private resetForm(contests: DOS.Contests) {
        const form: DOS.Form.SelectContests.FormData = {};

        _.forEach(contests, (c, _) => {
            form[c.id] = {
                audit: false,
                handCount: false,
                reason: { ...auditReasons[0] },
            };
        });

        this.setState({ form });
    }

    private onAuditChange = (contest: Contest) => () => {
        const s = { ...this.state };

        const { audit } = s.form[contest.id];
        s.form[contest.id].audit = !audit;

        this.setState(s);
    }

    private onFilterChange = (filter: string) => {
        this.setState({ filter });
    }

    private onHandCountChange = (contest: Contest) => () => {
        const s = { ...this.state };

        const { handCount } = s.form[contest.id];
        s.form[contest.id].handCount = !handCount;

        this.setState(s);
    }

    private onReasonChange = (contest: Contest) => (reason: DOS.Form.SelectContests.Reason) => {
        const s = { ...this.state };

        s.form[contest.id].reason = { ...reason };

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
