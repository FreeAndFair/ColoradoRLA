import { delay } from 'redux-saga';
import { SelectEffect } from 'redux-saga/effects';
import { cancel, fork, take } from 'redux-saga/effects';


type SelectPollDelay = () => number | IterableIterator<number | SelectEffect>;

function createPollSaga(
    pollActions: any[],
    pollStart: string,
    pollStop: string,
    selectPollDelay: SelectPollDelay,
) {
    function* pollTask() {
        while (true) {
            for (const a of pollActions) {
                yield a();
            }

            const pollDelay = yield selectPollDelay();

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
