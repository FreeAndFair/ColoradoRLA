import action from '.';


function roundSignOff(electors: any[]) {
    action('SUBMIT_ROUND_SIGN_OFF_SEND');

    const data = {
        received: { result: 'ok' },
        sent: electors,
    };
    action('SUBMIT_ROUND_SIGN_OFF_OK', data);
}


export default roundSignOff;
