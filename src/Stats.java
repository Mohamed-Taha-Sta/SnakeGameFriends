import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stats implements Serializable {

    Map<Socket, List<Integer>> playerStats;

    public Stats(Map<Socket, List<Integer>> playerStats) {
        this.playerStats = playerStats;
    }

    public Map<Socket, List<Integer>> getPlayerStats() {
        return playerStats;
    }

    public void setPlayerStats(Map<Socket, List<Integer>> playerStats) {
        this.playerStats = playerStats;
    }
}
