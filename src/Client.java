import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String host = "localhost";
        int port = 4444;
        Socket socket = new Socket(host, port);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        System.out.println("Connected to " + host + " on port " + port);

        Request request = new Request("Taha");

        out.writeObject(request);
        ImmediateResponse immediateResponse = (ImmediateResponse) in.readObject();

        if (immediateResponse.isResponse()) {
            Response response = (Response) in.readObject();
            Vector<Player> players = response.getPlayers();
            Player me = new Player(request.getName(), socket);
            System.out.println("Player "+me.getName()+" acquired initial infos.");
            for (Player player : players) {
                if (player.getPlayerSocket() == socket) {
                    me = player;
                    System.out.println("Player "+me.getName()+" acquired proper infos.");
                }
            }
            int handlerPort = (Integer) in.readObject();
            me.setHandlerPort(handlerPort);
            new GameFramePlayer(response.getPlayers(),me);


        }
    }
}
