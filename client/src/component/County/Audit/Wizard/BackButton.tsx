import * as React from 'react';


interface Props {
    back: OnClick;
}

const BackButton = ({ back }: Props) => {
    return <a className='pt-minimal' onClick={ back }>Back</a>;
};


export default BackButton;
