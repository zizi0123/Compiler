package util.error;

import util.position.Position;

public class SyntaxError extends RuntimeException{

    public String message;

    public Position pos;

    public SyntaxError(String msg,Position pos){
        this.message = msg;
        this.pos = pos;
    }

}
