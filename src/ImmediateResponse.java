import java.io.Serializable;

public class ImmediateResponse implements Serializable {
    private boolean response;

    public ImmediateResponse(boolean response) {
        this.response = response;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }
}
