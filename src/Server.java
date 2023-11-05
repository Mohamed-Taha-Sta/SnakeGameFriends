import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        ServerSocket serverSocket = new ServerSocket(4444);
        Handler handler = new Handler();
        Thread handlerThread = new Thread(handler);
        handlerThread.start();
        while(true){
            System.out.println("Snake Server Online");
            Socket clientSocket = serverSocket.accept();
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            Request request = (Request) in.readObject();
            if (!request.getName().isEmpty() && handler.getPlayers().size()<4){
                handler.addPlayer(new Player(request.getName(),clientSocket));
                out.writeObject(new ImmediateResponse(true));
            }else {
                out.writeObject(new ImmediateResponse(false));
            }


        }


    }
}
