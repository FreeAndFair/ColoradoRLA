import { endpoint } from 'corla/config';


export default (id: number) => {
    const fileInfo = { file_id: id };
    const encodedFileInfo = encodeURIComponent(JSON.stringify(fileInfo));
    const params = `file_info=${encodedFileInfo}`;
    const url = `${endpoint('download-file')}?${params}`;

    window.location.replace(url);
};
