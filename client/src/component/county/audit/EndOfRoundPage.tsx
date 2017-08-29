import * as React from 'react';

import CountyNav from '../Nav';

import EndOfRoundForm from './EndOfRoundForm';


class EndOfRoundPage extends React.Component<any, any> {
    public render() {
        return (
            <div>
                <CountyNav />
                <h3> End of Round [Round #]</h3>
                <div className='pt-card'>
                    Congratulations! You have completed reporting the votes on all ballots
                    randomly selected for this round of the risk-limiting audit of the
                    [County Name] County [Election Date] [Election Type] Election.
                </div>

                <div className='pt-card'>
                    Please complete this audit round by entering your names in the fields below,
                    making the following certification, and selecting Submit. By entering his or
                    her name and selecting Submit below, each Audit Board member individually
                    certifies that he or she:

                    <ul>
                        <li>
                            Personally located and retrieved, or personally observed a county staff
                            member locate and retrieve, each paper ballot randomly selected for
                            [audit round no.] of the [County Name] County [Election Date]
                            [Election Type] Election;
                        </li>
                        <li>
                            Personally examined each such randomly selected ballot;
                        </li>
                        <li>
                            Accurately entered the voter markings contained in each ballot contest
                            on each such randomly selected ballot, to the best of his or her ability;
                        </li>
                        <li>
                            Where applicable, resolved ambiguous markings, over votes and write-in
                            votes in accordance with the current version of the Secretary of State's
                            Voter Intent Guide;
                        </li>
                        <li>
                            In the case of physically duplicated ballots, if any, the Audit Board's
                            report reflects the voter markings on the paper ballot originally
                            submitted by the voter.
                        </li>
                    </ul>
                    <EndOfRoundForm />
                </div>
            </div>
        );
    }
}


export default EndOfRoundPage;
