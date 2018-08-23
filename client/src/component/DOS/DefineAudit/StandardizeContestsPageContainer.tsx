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
    const displayedContests: DOS.Contests = {};

    // XXX: Make this a map / filter chain. TypeScript complains about using
    // `DOS.Contests` in a `_.filter` context, and I do not know how to make it
    // happy.
    _.forEach(contests, (contest: Contest) => {
        const countyName = counties[contest.countyId].name;
        const countyStandards = canonicalContests[countyName] || [];

        if (!_.isEmpty(countyStandards) && !_.includes(countyStandards, contest.name)) {
          displayedContests[contest.id] = contest;
        }
    });

    return displayedContests;
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

/**
 * The URL path part for the page logically following this one.
 */
const NEXT_PAGE = '/sos/audit/select-contests';

/**
 * The URL path part for the page logically preceding this one.
 */
const PREVIOUS_PAGE = '/sos/audit';

class StandardizeContestsPageContainer extends React.Component<ContainerProps> {
    private forms: DOS.Form.StandardizeContests.Ref;

    public componentDidMount() {
        this.forms = { standardizeContestsForm: {} };
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

        const previousPage = () => history.push(PREVIOUS_PAGE);

        if (_.isEmpty(contests) || _.isEmpty(canonicalContests)) {
            return <WaitingForContests back={ previousPage } />;
        }

        if (dosState.asm === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const filteredContests = contestsToDisplay(contests, canonicalContests);

        if (_.isEmpty(filteredContests)) {
            return <Redirect to={ NEXT_PAGE } />;
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
                history.push(NEXT_PAGE);
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
