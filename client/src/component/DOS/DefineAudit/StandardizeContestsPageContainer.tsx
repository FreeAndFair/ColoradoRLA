import * as React from 'react';

import * as _ from 'lodash';

import { connect } from 'react-redux';

import { Redirect } from 'react-router-dom';

import { History } from 'history';

import Nav from '../Nav';

import standardizeContests from 'corla/action/dos/standardizeContests';

import { Breadcrumb, StandardizeContestsPage } from './StandardizeContestsPage';

import withDOSState from 'corla/component/withDOSState';
import withPoll from 'corla/component/withPoll';

import counties from 'corla/data/counties';

const contestsToDisplay = (contests: DOS.Contests,
                           canonicalContests: DOS.CanonicalContests) => {
    const cs: DOS.Contests = {};

    // XXX: Make this a map / filter chain. TypeScript complains about using
    // `DOS.Contests` in a `_.filter` context, and I do not know how to make it
    // happy.
    _.forEach(contests, (c: Contest) => {
        const countyName = counties[c.countyId].name;
        const countyStandards = canonicalContests[countyName] || [];

        if (!_.isEmpty(countyStandards) && !_.includes(countyStandards, c.name)) {
          cs[c.id] = c;
        }
    });

    return cs;
};

interface WaitingForContestsProps {
    back: OnClick;
}

const WaitingForContests = ({ back }: WaitingForContestsProps) => {
    return (
        <div>
            <Nav />
            <Breadcrumb />
            <div className='pt-card'>
                Waiting for counties to upload contest data.
            </div>
            <div>
                <button onClick={ back } className='pt-button pt-intent-primary pt-breadcrumb'>
                    Back
                </button>
                <button disabled className='pt-button pt-intent-primary pt-breadcrumb'>
                    Save & Next
                </button>
            </div>
        </div>
    );
};

interface ContainerProps {
    contests: DOS.Contests;
    canonicalContests: DOS.CanonicalContests;
    history: History;
    dosState: DOS.AppState;
}

class StandardizeContestsPageContainer extends React.Component<ContainerProps> {
    private areContestsLoaded: boolean;
    private areCanonicalContestsLoaded: boolean;
    private forms: DOS.Form.StandardizeContests.Ref;
    private nextPage: string;
    private previousPage: string;

    // XXX: This is *really bad* React. I know, and I'm sorry about it.
    public constructor(props: ContainerProps) {
        super(props);

        this.nextPage = '/sos/audit/select-contests';
        this.previousPage = '/sos/audit';
    }

    public componentDidMount() {
        this.areContestsLoaded = false;
        this.areCanonicalContestsLoaded = false;
        this.forms = { standardizeContestsForm: {} };
    }

    // XXX: Hack to work around waiting on a network to receive these new
    // props.
    public componentDidUpdate(prevProps: ContainerProps) {
        if (this.props.contests !== prevProps.contests) {
            this.areContestsLoaded = true;
        }

        if (this.props.canonicalContests !== prevProps.canonicalContests) {
            this.areCanonicalContestsLoaded = true;
        }
    }

    public render() {
        const {
            canonicalContests,
            contests,
            dosState,
            history,
        } = this.props;

        if (!_.get(dosState, 'asm')) {
            return <div />;
        }

        const previousPage = () => history.push(this.previousPage);

        if (!this.areContestsLoaded || !this.areCanonicalContestsLoaded) {
            return <WaitingForContests back={ previousPage } />;
        }

        if (dosState.asm === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const filteredContests = contestsToDisplay(contests, canonicalContests);

        if (_.isEmpty(filteredContests)) {
            return <Redirect to={ this.nextPage } />;
        }

        const props = {
            back: previousPage,
            canonicalContests,
            contests: filteredContests,
            // Pass this mutable thing into the child object so we can submit it
            // later. This is *not* the "right" way to do it.
            forms: this.forms,
            nextPage: () => {
                standardizeContests(this.forms.standardizeContestsForm || {});
                history.push(this.nextPage);
            },
        };

        return <StandardizeContestsPage { ...props } />;
    }
}

function select(dosState: DOS.AppState) {
    return {
        canonicalContests: dosState.canonicalContests,
        contests: dosState.contests,
        dosState,
    };
}

export default withPoll(
    withDOSState(StandardizeContestsPageContainer),
    'DOS_SELECT_CONTESTS_POLL_START',
    'DOS_SELECT_CONTESTS_POLL_STOP',
    select,
);
