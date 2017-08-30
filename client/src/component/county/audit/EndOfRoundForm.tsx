import * as React from 'react';

import { EditableText } from '@blueprintjs/core';


const ElectorFormField = (props: any) => {
    const {
        elector,
        index,
        onFirstNameChange,
        onLastNameChange,
        onTextConfirm,
    } = props;

    const { firstName, lastName } = elector;

    return (
        <div className='pt-card'>
            <h3>Audit Board Member #{index + 1}</h3>
            <div className='pt-card'>
                <label>
                    <span>
                        First Name:
                        <EditableText
                            className='pt-input'
                            value={ firstName }
                            onChange={ onFirstNameChange }
                            onConfirm={ onTextConfirm }
                        />
                    </span>
                    <span>
                        Last Name:
                        <EditableText
                            className='pt-input'
                            value={ lastName }
                            onChange={ onLastNameChange }
                            onConfirm={ onTextConfirm }
                        />
                    </span>
                </label>
            </div>
        </div>
    );
};

const EndOfRoundForm = (props: any) => {
    const {
        form,
        formIsValid,
        onFirstNameChange,
        onLastNameChange,
        onTextConfirm,
        submit,
    } = props;

    const createFormField = (index: number) => (
        <ElectorFormField
            index={ index }
            elector={ form[index] }
            onFirstNameChange={ onFirstNameChange(index) }
            onLastNameChange={ onLastNameChange(index) }
            onTextConfirm={ onTextConfirm }
        />
    );

    const disableSubmitButton = !formIsValid;

    return (
        <div>
            { createFormField(0) }
            { createFormField(1) }
            <button
                className='pt-button pt-intent-primary'
                disabled={ disableSubmitButton }
                onClick={ submit }
            >
                Submit
            </button>
        </div>
    );
};


export default EndOfRoundForm;
