import java.io.Serializable;
import java.util.UUID;

public class FileData implements Serializable {

    private static final long serialVersionUID = 1L;
    private UUID fileID;

    private String fileName;

    private String fileBase64;

    public FileData(UUID fileID, String fileName, String fileBase64){
        this.fileID = fileID;
        this.fileName = fileName;
        this.fileBase64 = fileBase64;
    }

    public UUID getFileID() {
        return fileID;
    }

    public void setFileID(UUID fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileBase64() {
        return fileBase64;
    }

    public void setFileBase64(String fileBase64) {
        this.fileBase64 = fileBase64;
    }
}
