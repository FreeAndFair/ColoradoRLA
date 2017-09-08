import action from '.';


interface CreateSubmitConfig {
    createData?: (sent: any, received: any) => any;
    failType: string;
    networkFailType: string;
    okType: string;
    sendType: string;
    url: string;
}


function defaultCreateData(sent: any, received: any): any {
    return { received, sent };
}

function createSubmitAction(config: CreateSubmitConfig) {
    const {
        failType,
        networkFailType,
        okType,
        sendType,
        url,
    } = config;

    const createData = config.createData || defaultCreateData;

    async function submitAction(sent: any) {
        action(sendType);

        const init: any = {
            body: JSON.stringify(sent),
            credentials: 'include',
            method: 'post',
        };

        try {
            const r = await fetch(url, init);

            if (!r.ok) {
                action(failType);

                if (r.status === 401) {
                    action('NOT_AUTHORIZED');
                }

                return;
            }

            const received = await r.json();
            const data = createData(sent, received);

            action(okType, data);
        } catch (e) {
            if (e.message === 'Failed to fetch') {
                action(networkFailType);
            }

            action('INTERNAL_ERROR');

            throw e;
        }
    }

    return submitAction;
}


export default createSubmitAction;
