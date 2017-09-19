import { delay } from 'redux-saga';
import { cancel, fork, take } from 'redux-saga/effects';


function createPollSaga(
    pollActions: any[],
    pollDelay: number,
    pollStart: string,
    pollStop: string,
) {
    function* pollTask() {
        while (true) {
            for (const a of pollActions) {
                a();
            }

            yield delay(pollDelay);
        }
    }

    function* pollSaga() {
        let poll = yield take(pollStart);

        while (poll) {
            const task = yield fork(pollTask);

            yield take(pollStop);

            yield cancel(task);

            poll = yield take(pollStart);
        }
    }

    return pollSaga;
}


export default createPollSaga;
