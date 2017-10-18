import * as cookie from 'js-cookie';


type SessionType = 'county' | 'dos';

interface Session {
    type: SessionType;
}

function isSession(o: any): o is Session {
    return o && 'type' in o;
}

function active(): boolean {
    const s = get();

    return !!s;
}

function expire() {
    cookie.remove('session');
}

function get(): Option<Session> {
    const s = cookie.getJSON('session');

    if (!isSession(s)) {
        return null;
    }

    return s;
}

function save(session: Session) {
    cookie.set('session', session);
}


export default { active, expire, get, save };
