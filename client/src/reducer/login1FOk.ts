function parseBox(box: string): LoginChallengeBox {
    const parts = box.substring(1, box.length - 1).split(',');

    const [x, y, ..._] = parts;

    return [x, y];
}

function parseChallenge(challenge: string): LoginChallenge {
    const boxes = challenge.split(' ');

    return boxes.map(parseBox);
}


function parse(data: any) {
    const { received, sent } = data;

    const dashboard: Dashboard = received.role === 'STATE'
        ? 'DOS'
        : 'County';

    const loginChallenge = received.challenge
        ? parseChallenge(received.challenge)
        : null;

    const { username } = sent;

    return { dashboard, loginChallenge, username };
}


export default function login1FOk(
    state: LoginAppState,
    action: Action.Login1FOk,
): LoginAppState {
    return {
        ...state,
        ...parse(action.data),
    };
}
