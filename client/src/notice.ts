import { Intent, Position, Toaster } from '@blueprintjs/core';

import {
    defaultDangerTimeout,
    defaultOkTimeout,
    defaultWarningTimeout,
} from 'corla/config';


const Toast = Toaster.create({ autoFocus: true });


function danger(message: string, timeout: number = defaultDangerTimeout) {
    Toast.show({
        intent: Intent.DANGER,
        message,
        timeout,
    });
}

function warning(message: string, timeout: number = defaultWarningTimeout) {
    Toast.show({
        intent: Intent.WARNING,
        message,
        timeout,
    });
}

function ok(message: string, timeout: number = defaultOkTimeout) {
    Toast.show({
        intent: Intent.SUCCESS,
        message,
        timeout,
    });
}


export default { danger, ok, warning };
