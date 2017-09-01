function parseBox(box: string): string[] {
    return box.substring(1, box.length - 1).split(',');
}

function parseChallenge(challenge: string): string[][] {
    const boxes = challenge.split(' ');

    return boxes.map(parseBox);
}


function parse(data: any) {
    const { received, sent } = data;

    const dashboard = received.role === 'STATE'
        ? 'sos'
        : 'county';

    const loginChallenge = received.challenge
        ? parseChallenge(received.challenge)
        : null;

    const { username } = sent;

    return { dashboard, loginChallenge, username };
}


export default (state: any, action: any) => ({
    ...state,
    ...parse(action.data),
});
