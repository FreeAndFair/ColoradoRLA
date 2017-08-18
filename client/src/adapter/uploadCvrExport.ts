export const parse = (formData: any) => ({
    fileName: formData.get('cvr_file').name,
    hash: formData.get('hash'),
});
