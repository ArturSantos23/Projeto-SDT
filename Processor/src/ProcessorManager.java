import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ProcessorManager extends UnicastRemoteObject implements ProcessorInterface {
    RequestClass request;
    FileData f;
    FileInterface FileInte = (FileInterface) Naming.lookup("rmi://localhost:2021/storage");
    protected ProcessorManager() throws RemoteException, MalformedURLException, NotBoundException {
    }

    public FileData GetProcessor(FileData p) throws RemoteException {
        return p;
    }

    public void Send(RequestClass r) throws IOException {
        request=r;
        if(request==null)
            return;

        f=FileInte.GetFile(request.getIdentificadorFile());
        if(f==null)
            return;
        Exec(r.getUrl());
    }

    public int GetEstado() throws RemoteException {
        if(request==null)
            return 0;
        else
            return request.getEstado();
    }

    public void Exec(String url) throws IOException
    {
        request.setEstadoConcluido();
        try {
            Process runtimeProcess = Runtime.getRuntime().exec(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(runtimeProcess.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null){
                output.append(line).append(System.getProperty("line.separator"));
            }
            runtimeProcess.waitFor();
            reader.close();

            System.out.println("Script executado!");

            FileInte.FileOutput(request.getIdentificadorFile().toString(), f);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
