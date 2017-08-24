import action from '.';


interface FileUploadActionConfig {
    createFormData: (...args: any[]) => FormData;
    createSent: (...args: any[]) => any;
    failType: string;
    networkFailType: string;
    okType: string;
    sendType: string;
    url: string;
}


function createFileUploadAction(config: FileUploadActionConfig) {
    const {
        createFormData,
        createSent,
        failType,
        networkFailType,
        okType,
        sendType,
        url,
    } = config;

    async function uploadAction(...args: any[]) {
        action(sendType);

        const formData = createFormData(...args);

        const init: any = {
            body: formData,
            credentials: 'include',
            method: 'post',
        };

        try {
            const r = await fetch(url, init);

            const body = await r.json();
            const received = { body, status: r.status };
            const sent = createSent(...args);
            const data = { received, sent };

            if (!r.ok) {
                action(failType, data);
                return;
            }

            action(okType, data);
        } catch (e) {
            if (e.message === 'Failed to fetch') {
                action(networkFailType);
            }

            action('INTERNAL_ERROR');

            throw e;
        }
    }

    return uploadAction;
}


export default createFileUploadAction;
