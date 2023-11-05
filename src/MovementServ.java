import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MovementServ implements Serializable {

    Map<Socket,Character> mouvements = new HashMap<>();

    public MovementServ(Map<Socket, Character> mouvements) {
        this.mouvements = mouvements;
    }



}
