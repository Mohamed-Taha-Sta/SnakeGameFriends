import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server_ver2 {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(4444);
        Vector<Player> playersServerSided = new Vector<>();
        Vector<Player> playersPlayerSided = new Vector<>();
        System.out.println("Snake Server Online");
        int playerNumber = 1;
        int i = 0;
        while (true) {
            if (playersServerSided.size() == playerNumber) {
                break;
            } else {
                // Accept client, create a player from said client
                Socket clientSocket = serverSocket.accept();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                System.out.println("Server treating the client " + clientSocket);
                Request request = (Request) in.readObject();
                if (!request.getName().isEmpty() && playersServerSided.size() < playerNumber) {
                    i = i + 1;
                    Player newPlayerServerSided = new Player(i, request.getName(), in, out);
                    Player newPlayerPlayerSided = new Player(i, request.getName());
                    playersServerSided.add(newPlayerServerSided);
                    playersPlayerSided.add(newPlayerPlayerSided);
                    out.writeObject(new ImmediateResponse(true));
                    out.writeObject(i);

                    out.writeObject(playerNumber);
                    if (playersServerSided.size() == playerNumber) {
                        for (Player player : playersServerSided) {
                            for (Player player1 : playersPlayerSided) {
                                player.getOut().writeObject(player1);
                            }
                        }
                    }
                    newPlayerServerSided.setReady(true);
                } else {
                    out.writeObject(new ImmediateResponse(false));
                }
            }
        }
        new GameFrame(playersServerSided);
    }
}
