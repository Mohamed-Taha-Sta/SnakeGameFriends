import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Response implements Serializable {
    private Vector<Player> players = new Vector<>();

    public Response(Vector<Player> players) {
        this.players = players;
    }

    public Vector<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Vector<Player> players) {
        this.players = players;
    }
}
