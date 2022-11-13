import java.io.Serializable;
import java.util.UUID;

public class FileData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fileID;

    private final String fileName;

    private final String fileBase64;

    public FileData(String fileID, String fileName, String fileBase64){
        this.fileID = fileID;
        this.fileName = fileName;
        this.fileBase64 = fileBase64;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileBase64() {
        return fileBase64;
    }
}
