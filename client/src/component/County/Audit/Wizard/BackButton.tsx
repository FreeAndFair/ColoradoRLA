import * as React from 'react';


interface Props {
    back: OnClick;
}

const BackButton = ({ back }: Props) => {
    return <button className='pt-button pt-intent-primary' onClick={ back }>Back</button>;
};


export default BackButton;
