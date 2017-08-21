export const parse = (data: any) => {
    const { sent } = data;

    return {
        fileName: sent.file.name,
        hash: sent.hash,
    };
};
