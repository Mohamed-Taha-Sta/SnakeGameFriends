import java.io.IOException;
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
        try {
            ServerSocket handlerSocket = new ServerSocket(handlerPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(Player player : players){
            try {
                ObjectOutputStream out = new ObjectOutputStream(player.getPlayerSocket().getOutputStream());
                out.writeObject(handlerPort);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        try {
            new GameFrame(players);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
