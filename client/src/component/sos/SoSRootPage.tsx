import * as React from 'react';

import SoSNav from './Nav';


const SoSRootPage = () => {
    return (
        <div>
            <SoSNav />
            <div>
                Notifications.
            </div>
            <div>
                <div>
                    <h3>County Updates</h3>
                    <div>
                        County Ballot Manifests, CVRs and Hashes (status & download links)
                    </div>
                    <div>
                        [Ballot Order]
                        [For each audited contest, total number of ballots to be audited
                        (including previous rounds)]
                    </div>
                    <div>
                        Updates based on county progress, discrepencies.
                    </div>
                </div>
                <div>
                    <h3>Contest updates.</h3>
                    <div>
                        Target and Current Risk Limits by Contest
                    </div>
                    <div>
                        Status (audit required, audit in progress, audit complete, hand
                        count required, hand count complete) by Audited Contest
                    </div>
                    <div>
                        [Seed for randomization]
                    </div>
                    <div>
                        List of Audit Rounds (number of ballots, status by County, download links)
                    </div>
                    <div>
                        Link to Final Audit Report
                    </div>
                </div>
            </div>
        </div>
    );
}


export default SoSRootPage;
