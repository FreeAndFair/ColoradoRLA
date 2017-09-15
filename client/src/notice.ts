import { Intent, Position, Toaster } from '@blueprintjs/core';


const Toast = Toaster.create({ autoFocus: true });


const DEFAULT_TIMEOUT = 5000;


function danger(message: string, timeout: number = DEFAULT_TIMEOUT) {
    Toast.show({
        intent: Intent.DANGER,
        message,
        timeout,
    });
}

function warning(message: string, timeout: number = DEFAULT_TIMEOUT) {
    Toast.show({
        intent: Intent.WARNING,
        message,
        timeout,
    });
}

function ok(message: string, timeout: number = DEFAULT_TIMEOUT) {
    Toast.show({
        intent: Intent.SUCCESS,
        message,
        timeout,
    });
}


export default { danger, ok, warning };
