export const parse = (formData: any) => ({
    fileName: formData.get('bmi_file').name,
    hash: formData.get('hash'),
});
