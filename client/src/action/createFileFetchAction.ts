import action from '.';


interface CreateFetchConfig {
    failType: string;
    networkFailType: string;
    okType: string;
    sendType: string;
    url: string;
}


function createFileFetchAction(config: CreateFetchConfig) {
    const {
        failType,
        networkFailType,
        okType,
        sendType,
        url,
    } = config;

    async function fetchAction() {
        action(sendType);

        const init: any = {
            credentials: 'include',
            method: 'get',
        };

        try {
            const r: any = await fetch(url, init);

            if (!r.ok) {
                action(failType);
            }

            if (r.status === 401) {
                action('NOT_AUTHORIZED');
            }

            const blob = await r.blob();

            const fileUrl = await URL.createObjectURL(blob);
            window.open(fileUrl, '_blank');

            const data = { file: blob };
            action(okType, data);
        } catch (e) {
            if (e.message === 'Failed to fetch') {
                action(networkFailType);
            }

            action('INTERNAL_ERROR');

            throw e;
        }
    }

    return fetchAction;
}


export default createFileFetchAction;
