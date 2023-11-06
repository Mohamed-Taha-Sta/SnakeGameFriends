import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Handler implements Runnable{

    Vector<Player> players = new Vector<>();

    public Vector<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    @Override
    public void run() {
//        players.add(new Player("salah",new Socket()));
//        players.add(new Player("monji",new Socket()));
//        players.add(new Player("ziad",new Socket()));
//        players.add(new Player("Mohamed", new Socket()));
        int handlerPort = 4445;
        ServerSocket handlerSocket;
        Socket clientSocket;
        try {
            handlerSocket = new ServerSocket(handlerPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (players.size()<1){
            try {
                System.out.println("here");
                clientSocket = handlerSocket.accept();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                String s = (String) in.readObject();
                System.out.println(s);
                System.out.println("here2");
                System.out.println(players.size()+" inside while");
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(players.size()+" Out while");

//
//        for(Player player : players){
//            try {
//                ObjectOutputStream out = new ObjectOutputStream(player.getHandlerPlayerSocket().getOutputStream());
//                out.writeObject(handlerPort);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        try {
            new GameFrame(players);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
