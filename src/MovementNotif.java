import java.io.Serializable;

public class MovementNotif implements Serializable {

    private char Direction;

    public MovementNotif(char direction) {
        Direction = direction;
    }
}
