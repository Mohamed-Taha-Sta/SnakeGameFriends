import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class Client_ver2 {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        String host;
        int port;
        String playerName;
        ClientMainMenu clientMainMenu = new ClientMainMenu();

        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        boolean connected = false;

        do {
            clientMainMenu.setVisible(true);
            synchronized (clientMainMenu.getLock()) {
                clientMainMenu.getLock().wait();
            }

            String[] connectionDetails = clientMainMenu.getConnectionDetails();
            host = connectionDetails[1];
            port = Integer.parseInt(connectionDetails[2]);
            playerName = connectionDetails[0];
            clientMainMenu.setVisible(false);


            try {
                Socket socket = new Socket(host, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                connected = true;
            } catch (Exception e) {
                System.out.println("Timeout");
            }
        }while (!connected);

        System.out.println("Connected to " + host + " on port " + port);
        Vector<Player> players = new Vector<>();

        out.writeObject(new Request(playerName));

        ImmediateResponse immediateResponse = (ImmediateResponse) in.readObject();
        if (immediateResponse.isResponse()) {

            int my_id = (Integer) in.readObject();

            Integer playerNumber = (Integer) in.readObject();
            for (int i = 0; i < playerNumber; i++) {
                players.add((Player) in.readObject());
            }

            new GameFramePlayer(players, my_id, in, out);

        } else {
            System.out.println("I was not admitted");
        }

    }


}
