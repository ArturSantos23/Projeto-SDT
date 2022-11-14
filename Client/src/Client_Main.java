import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Client_Main {
    static Scanner input = new Scanner(System.in);
    static FileInterface fileInterface;
    static BalancerInterface balancerInterface;
    static ProcessorInterface processorInterface;
    static {
        try {
            fileInterface = (FileInterface) Naming.lookup("rmi://localhost:2021/storage");
            balancerInterface = (BalancerInterface) Naming.lookup("rmi://localhost:2025/balancer");
            processorInterface =(ProcessorInterface) Naming.lookup("rmi://localhost:2022/processor");
        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String FileToBase64(File file){
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            //System.out.println("bytes: " + Arrays.toString(fileContent));
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }

    public static void getEstado() throws RemoteException{
        int state = 0;
        state = processorInterface.GetEstado();
        if(state == 0){
            System.out.println("Não enviado!");
        }
        else if(state == 2){
            System.out.println("Processo concluído!");
        }
    }

    public static void sendFile() throws IOException{
        try {
            File path;
            String ID;
            path = new File("C:\\Users\\aguia\\Desktop\\100lines.txt");
            String base64 = FileToBase64(path);
            FileData f = new FileData(null, "100lines.txt", base64);
            ID = fileInterface.addFile(f);
            System.out.println("File ID: " + ID);
        } catch (RemoteException e){
            System.out.println(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getFile() throws IOException{
        String ID = null;
        System.out.println("ID:");
        ID=input.next();
        FileData f = fileInterface.GetFile(ID);
        if (f==null){
            System.out.println(f);
            return;
        }
        else{
            System.out.println("Nome do ficheiro: ("+f.getFileName()+")");
        }

    }

    public static void createRequest() throws IOException, NotBoundException, InterruptedException {
        File path;
        System.out.println("Insira o ID do ficheiro a enviar");
        String ID = input.next();
        System.out.println("Insira o URL do script a executar");
        String url = input.nextLine();
        url += input.nextLine();
        path = new File(url);
        if(path.isFile()==false){
            return;
        }
        else {
            System.out.println("Cheguei aqui");
            ArrayList<String> r = balancerInterface.SendRequest(ID, url);
        }

    }

    public static void Menu() throws IOException, NotBoundException, InterruptedException {
        int op;

        System.out.println("1 - Enviar ficherio para Storage");
        System.out.println("2 - Receber um ficheiro dado o seu ID");
        System.out.println("3 - Enviar um request");
        System.out.println("4 - Saber o estado do pedido");

        op = parseInt(input.next());
        switch (op){
            case 1:
                sendFile();
                break;
            case 2:
                //GetFile
                getFile();
                break;
            case 3:
                //CreateRequest
                createRequest();
                break;
            case 4:
                //getEstado
                getEstado();
                break;
            default:
                System.out.println("Escolha uma opção válida\n\n");
                Menu();
        }
    }
    public static void main(String[] args) throws NotBoundException, IOException, InterruptedException {
        /*FileInterface l;
        File f = new File("C:\\Users\\artur\\Desktop\\Artur\\Teste.png");
        String base64 = FileToBase64(f);
        String UUID = null;
        //String[] serverName = null;

        try{
            //String[] serverIdSplited = serverId.split("//",2);
            //serverName = serverIdSplited[1].split("/",2);

            l = (FileInterface) Naming.lookup("rmi://localhost:2022/processor");
            FileData fd = new FileData(null, "Teste.png", base64);
            UUID = l.addFile(fd);

        } catch (ConnectException e) {
            //System.out.println("O servidor ["+serverName[0]+"] não consegiu dar resposta.");
            return;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Número máximo de respostas atingidas");
            return;
        } catch(RemoteException e) {
            System.out.println(e.getMessage());
        } catch(Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Resposta dada com sucesso pelo servidor: "+serverName[0]);
        System.out.print("Identificador do ficheiro: ");
        System.out.println(UUID+"\n");*/
        Menu();
    }


}
