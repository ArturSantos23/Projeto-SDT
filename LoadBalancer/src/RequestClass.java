import java.io.Serializable;
import java.util.UUID;

public class RequestClass implements Serializable {
    private final UUID IdentificadorRequest;
    private final String  Script;
    private final String IdentificadorFile;
    private  UUID IdentificadorProcessor;

    private int Estado; //1->em espera 0->concluido

    public RequestClass (UUID IdentificadorRequest, String script, String IdentificadorFile, int Estado)
    {
        this.IdentificadorRequest=IdentificadorRequest;
        this.Script=script;
        this.IdentificadorFile=IdentificadorFile;
        this.Estado=1;
    }
    void setIdentificadorProcessor(UUID Processor)
    {
        this.IdentificadorProcessor=Processor;
    }
    UUID getIdentificadorProcessor()
    {
        return this.IdentificadorProcessor;
    }
    UUID getIdentificadorRequest()
    {
        return this.IdentificadorRequest;
    }
    String getIdentificadorFile()
    {
        return this.IdentificadorFile;
    }
    public  int getEstado()
    {
        return this.Estado;
    }
    public  void setEstadoProcessamento()
    {
        this.Estado=3;
    }
    public  void setEstadoConcluido()
    {
        this.Estado=2;
    }
    String getUrl()
    {
        return this.Script;
    }
}
