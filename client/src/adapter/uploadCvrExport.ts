export const parse = (sent: any) => ({
    fileName: sent.file.name,
    hash: sent.hash,
});
