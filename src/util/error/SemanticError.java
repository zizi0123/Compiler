package util.error;

import util.position.Position;

public class SemanticError extends RuntimeException{
    public String message;

    public Position pos;


    public SemanticError(String msg,Position pos){
        this.pos = pos;
        this.message = msg;
    }
}
