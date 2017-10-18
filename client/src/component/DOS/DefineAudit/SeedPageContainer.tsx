import * as React from 'react';
import { Redirect } from 'react-router-dom';

import { History } from 'history';

import withSync from 'corla/component/withSync';

import SeedPage from './SeedPage';

import uploadRandomSeed from 'corla/action/dos/uploadRandomSeed';


interface ContainerProps {
    history: History;
    publicMeetingDate: Date;
    seed: string;
    sos: DosState;
}


class SeedPageContainer extends React.Component<ContainerProps> {
    public render() {
        const { history, publicMeetingDate, seed, sos } = this.props;

        if (!sos) {
            return <div />;
        }

        if (sos.asm.currentState === 'DOS_AUDIT_ONGOING') {
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

function select(state: AppState) {
    const { sos } = state;

    if (!sos) { return {}; }

    return {
        publicMeetingDate: sos.publicMeetingDate,
        seed: sos.seed,
        sos,
    };
}


export default withSync(
    SeedPageContainer,
    'DOS_DEFINE_AUDIT_RANDOM_SEED_SYNC',
    select,
);
