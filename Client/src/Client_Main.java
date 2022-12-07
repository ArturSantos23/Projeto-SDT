import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Client_Main {
    static RequestClass requestClass;
    final static Scanner input = new Scanner(System.in);
    final static FileInterface fileInterface;
    final static BalancerInterface balancerInterface;

    static {
        try {
            fileInterface = (FileInterface) Naming.lookup("rmi://localhost:2021/storage");
            balancerInterface = (BalancerInterface) Naming.lookup("rmi://localhost:2025/balancer");
        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String FileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            //System.out.println("bytes: " + Arrays.toString(fileContent));
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }

    public static void getEstado() throws RemoteException {
        int state;
        state = balancerInterface.getProcEstado();
        if (state == 0) {
            System.out.println("Não enviado!");
        } else if (state == 2) {
            System.out.println("Processo concluído!");
        }
    }

    public static void sendFile() {
        try {
            File path;
            String ID;
            path = new File("C:\\Users\\aguia\\Desktop\\BINO.txt");
            String base64 = FileToBase64(path);
            FileData f = new FileData(null, "BINO.txt", base64);
            ID = fileInterface.addFile(f);
            System.out.println("File ID: " + ID);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getFile() throws IOException {
        String ID;
        System.out.println("ID:");
        ID = input.next();
        FileData f = fileInterface.getFile(ID);
        if (f == null) {
            System.out.println((Object) null);
        } else {
            System.out.println("Nome do ficheiro: (" + f.getFileName() + ")");
        }
    }

    public static void createRequest() throws IOException, InterruptedException {
        String fileID;
        System.out.println("Insira o ID do ficheiro a enviar");
        fileID = input.next();
        FileData f = fileInterface.getFile(fileID);
        ArrayList<String> outputContent = balancerInterface.SendRequest(fileID, f.getFileName());

        for (String s : outputContent) {
            System.out.println(s);
        }
    }

    public static void Menu() throws IOException, InterruptedException {
        int op;

        System.out.println("1 - Enviar ficherio para Storage");
        System.out.println("2 - Receber um ficheiro dado o seu ID");
        System.out.println("3 - Executar script");
        System.out.println("4 - Saber o estado do pedido");

        op = parseInt(input.next());
        switch (op) {
            case 1:
                sendFile();
                break;
            case 2:
                getFile();
                break;
            case 3:
                createRequest();
                break;
            case 4:
                getEstado();
                break;
            default:
                System.out.println("Escolha uma opção válida\n\n");
                Menu();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Menu();
    }
}
