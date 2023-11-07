import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class Client_ver2 {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String host = "localhost";
        int port = 4444;
        Socket socket = new Socket(host, port);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connected to " + host + " on port " + port);
        Vector<Player> players = new Vector<>();

        out.writeObject(new Request("Taha"));

        ImmediateResponse immediateResponse = (ImmediateResponse) in.readObject();
        System.out.println("Client here 1");
        if (immediateResponse.isResponse()) {
            System.out.println("Client here 2");

            int my_id = (Integer) in.readObject();
            System.out.println("Client here 3");

            Integer playerNumber = (Integer) in.readObject();
            System.out.println(playerNumber);
            for (int i = 0; i < playerNumber; i++) {
                players.add((Player) in.readObject());
            }

            System.out.println("Client here 4");

            System.out.println("client here");
            new GameFramePlayer(players,my_id,in,out);

        }else {
            System.out.println("I was not admitted");
        }

    }


}
