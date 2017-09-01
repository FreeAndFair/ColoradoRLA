function parseBox(box: string): string[] {
    return box.substring(1, box.length - 1).split(',');
}

function parseChallenge(challenge: string): string[][] {
    const boxes = challenge.split(' ');

    return boxes.map(parseBox);
}


function parse(data: any) {
    const dashboard = data.role === 'STATE'
        ? 'sos'
        : 'county';

    const loginChallenge = data.challenge
        ? parseChallenge(data.challenge)
        : null;

    return { dashboard, loginChallenge };
}


export default (state: any, action: any) => ({
    ...state,
    ...parse(action.data.received),
});
