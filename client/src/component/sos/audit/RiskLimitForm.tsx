import * as React from 'react';

import { NumericInput } from '@blueprintjs/core';


interface FormProps {
    forms: any;
}

interface FormState {
    ballotPollingLimit: number;
    comparisonLimit: number;
}


const DEFAULT_RISK_LIMIT = 0.05;


class RiskLimitForm extends React.Component<FormProps & any, FormState> {
    public state: FormState = {
        ballotPollingLimit: DEFAULT_RISK_LIMIT,
        comparisonLimit: DEFAULT_RISK_LIMIT,
    };

    public render() {
        const { ballotPollingLimit, comparisonLimit } = this.state;

        this.props.forms.riskLimit = this.state;

        return (
            <div>
                <div>
                    <label>
                        Ballot Polling Audits
                        <NumericInput
                            minorStepSize={ 0.001 }
                            stepSize={ 0.01 }
                            value={ ballotPollingLimit }
                            onValueChange={ this.onBallotPollingValueChange } />
                    </label>
                </div>
                <div>
                    <label>
                        Comparison Audits
                        <NumericInput
                            minorStepSize={ 0.001 }
                            stepSize={ 0.01 }
                            value={ comparisonLimit }
                            onValueChange={ this.onComparisonValueChange } />
                    </label>
                </div>
            </div>
        );
    }

    private onBallotPollingValueChange = (limit: number) => {
        const s = { ...this.state };

        s.ballotPollingLimit = limit;

        this.setState(s);
    }

    private onComparisonValueChange = (limit: number) => {
        const s = { ...this.state };

        s.comparisonLimit = limit;

        this.setState(s);
    }
}


export default RiskLimitForm;
