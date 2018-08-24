import * as React from 'react';

import { NumericInput } from '@blueprintjs/core';

import setNumberOfAuditBoards from 'corla/action/county/setNumberOfAuditBoards';


/**
 * Minimum number of audit boards that can participate.
 */
const MIN_AUDIT_BOARDS = 1;

interface AuditBoardNumberSelectorProps {
    auditBoardCount: number;
    numberOfBallotsToAudit?: number;
    isShown: boolean;
    isEnabled: boolean;
}

interface AuditBoardNumberSelectorState {
    auditBoardCount: number;
    isEnabled: boolean;
}

class AuditBoardNumberSelector
    extends React.Component<AuditBoardNumberSelectorProps,
                            AuditBoardNumberSelectorState> {
    constructor(props: AuditBoardNumberSelectorProps) {
        super(props);

        this.state = {
          auditBoardCount: props.auditBoardCount,
          isEnabled: props.isEnabled
        };

        this.handleChangeAuditBoards = this.handleChangeAuditBoards.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChangeAuditBoards(asNumber: number, asString: string) {
        if (asNumber >= 1) {
          this.setState({ auditBoardCount: asNumber });
        }
    }

    handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();

        const { auditBoardCount } = this.state;

        this.setState({ isEnabled: false });
        setNumberOfAuditBoards({ auditBoardCount });
    }

    helperText(toAudit?: number) {
        if (!toAudit) {
            return null;
        }

        return (
            <div className='pt-form-helper-text'>
                There are <b>{ toAudit }</b>  ballot cards to audit in this
                round.
            </div>
        );
    }

    render() {
        const { isShown, numberOfBallotsToAudit } = this.props;
        const { isEnabled } = this.state;

        if (!isShown) {
            return null;
        }

        return (
            <div className='pt-card'>
                <form onSubmit={ this.handleSubmit }>
                    <div className='pt-form-group'>
                        <label className='pt-label pt-ui-text-large'
                               htmlFor='number-of-audit-boards-input'>
                            How many audit boards will be auditing?
                        </label>
                        <div className='pt-control-group'>
                            <NumericInput id='number-of-audit-boards-input'
                                          min={ MIN_AUDIT_BOARDS }
                                          value={ this.state.auditBoardCount }
                                          onValueChange={ this.handleChangeAuditBoards }
                                          disabled={ !isEnabled } />
                            <button disabled={ !isEnabled } className='pt-button pt-intent-primary'>Enter</button>
                        </div>
                        { this.helperText(numberOfBallotsToAudit) }
                    </div>
                </form>
            </div>
        );
    }
};

export default AuditBoardNumberSelector;
