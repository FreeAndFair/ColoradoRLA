import * as React from 'react';

import { isFinite } from 'lodash';

import { NumericInput } from '@blueprintjs/core';


interface FormProps {
    forms: any;
}

interface FormState {
    ballotPollingField: string;
    ballotPollingLimit: number;
    comparisonField: string;
    comparisonLimit: number;
}


const DEFAULT_RISK_LIMIT = 0.05;
const MIN_RISK_LIMIT = 0.0001;
const MAX_RISK_LIMIT = 1 - MIN_RISK_LIMIT;


function isValidRiskLimit(limit: number) {
    return isFinite(limit)
        && MIN_RISK_LIMIT <= limit
        && limit <= MAX_RISK_LIMIT;
}


class RiskLimitForm extends React.Component<FormProps & any, FormState> {
    public state: FormState = {
        ballotPollingField: `${DEFAULT_RISK_LIMIT}`,
        ballotPollingLimit: DEFAULT_RISK_LIMIT,
        comparisonField: `${DEFAULT_RISK_LIMIT}`,
        comparisonLimit: DEFAULT_RISK_LIMIT,
    };

    public render() {
        const {
            ballotPollingField,
            ballotPollingLimit,
            comparisonField,
            comparisonLimit,
        } = this.state;

        this.props.forms.riskLimit = this.state;

        return (
            <div>
                <div>
                    <label>
                        Ballot Polling Audits
                        <NumericInput
                            allowNumericCharactersOnly={ true }
                            min={ MIN_RISK_LIMIT }
                            max={ MAX_RISK_LIMIT }
                            minorStepSize={ 0.001 }
                            onBlur={ this.onBlur }
                            stepSize={ 0.01 }
                            value={ ballotPollingField }
                            onValueChange={ this.onBallotPollingValueChange } />
                    </label>
                </div>
                <div>
                    <label>
                        Comparison Audits
                        <NumericInput
                            allowNumericCharactersOnly={ true }
                            min={ MIN_RISK_LIMIT }
                            max={ MAX_RISK_LIMIT }
                            minorStepSize={ 0.001 }
                            onBlur={ this.onBlur }
                            stepSize={ 0.01 }
                            value={ comparisonField }
                            onValueChange={ this.onComparisonValueChange } />
                    </label>
                </div>
            </div>
        );
    }

    private onBlur = () => {
        const s = { ...this.state };

        const parsedBallotPollingField = parseFloat(s.ballotPollingField);
        if (isValidRiskLimit(parsedBallotPollingField)) {
            s.ballotPollingLimit = parsedBallotPollingField;
        } else {
            s.ballotPollingField = `${s.ballotPollingLimit}`;
        }

        const parsedComparisonField = parseFloat(s.comparisonField);
        if (isValidRiskLimit(parsedComparisonField)) {
            s.comparisonLimit = parsedComparisonField;
        } else {
            s.comparisonField = `${s.comparisonLimit}`;
        }

        this.setState(s);
    }

    private onBallotPollingValueChange = (_: number, field: string) => {
        const s = { ...this.state };

        s.ballotPollingField = field;

        this.setState(s);
    }

    private onComparisonValueChange = (_: number, field: string) => {
        const s = { ...this.state };

        s.comparisonField = field;

        this.setState(s);
    }
}


export default RiskLimitForm;
