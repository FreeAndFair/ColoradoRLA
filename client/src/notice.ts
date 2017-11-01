import { Intent, Position, Toaster } from '@blueprintjs/core';


const Toast = Toaster.create({ autoFocus: true });


function danger(message: string, timeout: number = 0) {
    Toast.show({
        intent: Intent.DANGER,
        message,
        timeout,
    });
}

function warning(message: string, timeout: number = 0) {
    Toast.show({
        intent: Intent.WARNING,
        message,
        timeout,
    });
}

function ok(message: string, timeout: number = 10000) {
    Toast.show({
        intent: Intent.SUCCESS,
        message,
        timeout,
    });
}


export default { danger, ok, warning };
