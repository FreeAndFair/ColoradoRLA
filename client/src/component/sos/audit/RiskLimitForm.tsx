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
        && MIN_RISK_LIMIT <= fromPercent(limit)
        && fromPercent(limit) <= MAX_RISK_LIMIT;
}

function fromPercent(val: number) {
    return val / 100;
}

function toPercent(val: number) {
    return val * 100;
}


class RiskLimitForm extends React.Component<FormProps & any, FormState> {
    public state: FormState = {
        ballotPollingField: `${toPercent(DEFAULT_RISK_LIMIT)}`,
        ballotPollingLimit: DEFAULT_RISK_LIMIT,
        comparisonField: `${toPercent(DEFAULT_RISK_LIMIT)}`,
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

        const ballotPollingFormField = (
            <label>
                Ballot Polling Audits (%)
                <NumericInput
                    allowNumericCharactersOnly={ true }
                    min={ toPercent(MIN_RISK_LIMIT) }
                    max={ toPercent(MAX_RISK_LIMIT) }
                    minorStepSize={ toPercent(0.001) }
                    onBlur={ this.onBlur }
                    stepSize={ toPercent(0.01) }
                    value={ ballotPollingField }
                    onValueChange={ this.onBallotPollingValueChange } />
            </label>
        );

        const comparisonFormField = (
            <label>
                Comparison Audits (%)
                <NumericInput
                    allowNumericCharactersOnly={ true }
                    min={ toPercent(MIN_RISK_LIMIT) }
                    max={ toPercent(MAX_RISK_LIMIT) }
                    minorStepSize={ toPercent(0.001) }
                    onBlur={ this.onBlur }
                    stepSize={ toPercent(0.01) }
                    value={ comparisonField }
                    onValueChange={ this.onComparisonValueChange } />
            </label>
        );

        return (
            <div className='pt-card'>
                { comparisonFormField }
            </div>
        );
    }

    private onBlur = () => {
        const s = { ...this.state };

        const parsedBallotPollingField = parseFloat(s.ballotPollingField);
        if (isValidRiskLimit(parsedBallotPollingField)) {
            s.ballotPollingLimit = fromPercent(parsedBallotPollingField);
        } else {
            s.ballotPollingField = `${toPercent(s.ballotPollingLimit)}`;
        }

        const parsedComparisonField = parseFloat(s.comparisonField);
        if (isValidRiskLimit(parsedComparisonField)) {
            s.comparisonLimit = fromPercent(parsedComparisonField);
        } else {
            s.comparisonField = `${toPercent(s.comparisonLimit)}`;
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
