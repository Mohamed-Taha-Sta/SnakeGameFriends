import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Player implements Serializable {

    int[] x = new int[GamePanel.GAME_UNITS];
    int[] y = new int[GamePanel.GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten = 0;
    private char direction = 'R';
    private boolean running = false;
    private boolean ready = false;
    private String name;
    private Color startColor;
    private Color endColor;
//    private Socket playerSocket;
//    private Socket handlerPlayerSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int handlerPort;

//    public Socket getHandlerPlayerSocket() {
//        return handlerPlayerSocket;
//    }
//
//    public void setHandlerPlayerSocket(Socket handlerPlayerSocket) {
//        this.handlerPlayerSocket = handlerPlayerSocket;
//    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public int getHandlerPort() {
        return handlerPort;
    }

    public void setHandlerPort(int handlerPort) {
        this.handlerPort = handlerPort;
    }

    public Player(String name, ObjectInputStream in, ObjectOutputStream out) {
        this.name = name;
        this.in = in;
        this.out = out;
    }

    public Player(String name) {
        this.name = name;
    }

    public int[] getX() {
        return x;
    }

    public void setX(int[] x) {
        this.x = x;
    }

    public int[] getY() {
        return y;
    }

    public void setY(int[] y) {
        this.y = y;
    }

    public int getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(int bodyParts) {
        this.bodyParts = bodyParts;
    }

    public int getApplesEaten() {
        return applesEaten;
    }

    public void setApplesEaten(int applesEaten) {
        this.applesEaten = applesEaten;
    }

    public char getDirection() {
        return direction;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Socket getPlayerSocket() {
//        return playerSocket;
//    }
//
//    public void setPlayerSocket(Socket playerSocket) {
//        this.playerSocket = playerSocket;
//    }

    public Color getStartColor() {
        return startColor;
    }

    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }

    public Color getEndColor() {
        return endColor;
    }

    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }


    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
//                ", playerSocket=" + playerSocket +
                '}';
    }
}
