import { Intent, Position, Toaster } from '@blueprintjs/core';


const Toast = Toaster.create({ autoFocus: true });


function danger(message: string) {
    Toast.show({
        intent: Intent.DANGER,
        message,
    });
}

function ok(message: string) {
    Toast.show({
        intent: Intent.SUCCESS,
        message,
    });
}


export default { danger, ok };
