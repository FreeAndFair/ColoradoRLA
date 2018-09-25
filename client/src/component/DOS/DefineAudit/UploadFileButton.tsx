import * as React from 'react';
import Dropzone from 'react-dropzone';

interface UploadFileButtonProps {
  forms: DOS.Form.AuditDef.Forms;
}

interface UploadFileButtonState {
  files: object[];
}

const dropStyle = {
  border: '2px dashed rgb(102, 102, 102)',
  borderRadius: '5px',
  marginBottom: '10px',
  padding: '1em',
  width: '500px',
};

const activeStyle = {
  backgroundColor: 'rgb(245, 245, 245)',
  border: '2px solid green',
  borderRadius: '5px',
};

class UploadFileButton extends React.Component<UploadFileButtonProps, UploadFileButtonState> {
  constructor() {
    super();
    this.state = { files: [] };
  }
  public render() {
    this.props.forms.uploadFile = this.state;

    return (
      <div>
        <div className='dropzone'>
          <Dropzone
            onDrop={this.onDrop.bind(this)}
            activeStyle={activeStyle}
            multiple={false}
            style={dropStyle}>
            <div>
              <strong>
            Drag and drop or click here to select the file you wish to
            use as the source for standardized contest names across jurisdictions.
            </strong>
              <div>File requirements:</div>
              <ul>
                <li>File must be CSV formatted, with a <em>.csv</em> or <em>.txt</em>
                    &nbsp;extension. Other file types are not accepted</li>
                <li>The file must contain a header row consisting of <em>CountyName</em>
                    &nbsp;and <em>ContestName</em>.</li>
                <li>The file may not contain duplicate records</li>
              </ul>
            </div>
          </Dropzone>
        </div>

        <aside>
          <div className='import-file'>
            <strong>Ready to import: </strong>
            { this.state.files.map(
                (file: any) => <span key={file.name}>{file.name} ({file.size} bytes.)</span>)
            }
          </div>
        </aside>
      </div>
    );
  }

  private onDrop(files: any) {
    files.forEach((file: any) => {
      const reader = new FileReader();
      reader.onload = () => { file.contents = reader.result; };
      reader.readAsBinaryString(file);
    });

    this.setState({ files });

    const importDiv = document.querySelector('.import-file');
    if (importDiv) {
      importDiv.setAttribute('style', 'display: inline;');
    }
  }
}

export default UploadFileButton;
