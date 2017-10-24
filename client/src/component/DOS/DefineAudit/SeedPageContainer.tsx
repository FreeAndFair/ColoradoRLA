import * as React from 'react';
import { Redirect } from 'react-router-dom';

import { History } from 'history';

import withDOSState from 'corla/component/withDOSState';
import withSync from 'corla/component/withSync';

import SeedPage from './SeedPage';

import uploadRandomSeed from 'corla/action/dos/uploadRandomSeed';


interface ContainerProps {
    dosState: DOS.AppState;
    history: History;
    publicMeetingDate: Date;
    seed: string;
}


class SeedPageContainer extends React.Component<ContainerProps> {
    public render() {
        const { history, publicMeetingDate, seed, dosState } = this.props;

        if (!dosState) {
            return <div />;
        }

        if (!dosState.asm) {
            return <div />;
        }

        if (dosState.asm === 'DOS_AUDIT_ONGOING') {
            return <Redirect to='/sos' />;
        }

        const props = {
            back: () => history.push('/sos/audit/select-contests'),
            nextPage: () => history.push('/sos/audit/review'),
            publicMeetingDate,
            seed,
            uploadRandomSeed,
        };

        return <SeedPage { ...props } />;
    }
}

function select(dosState: DOS.AppState) {
    if (!dosState) { return {}; }

    return {
        dosState,
        publicMeetingDate: dosState.publicMeetingDate,
        seed: dosState.seed,
    };
}


export default withSync(
    withDOSState(SeedPageContainer),
    'DOS_DEFINE_AUDIT_RANDOM_SEED_SYNC',
    select,
);
