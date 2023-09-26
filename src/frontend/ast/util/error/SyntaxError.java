package frontend.ast.util.error;

import frontend.ast.util.position.Position;

public class SyntaxError extends RuntimeException{

    public String message;

    public Position pos;

    public SyntaxError(String msg,Position pos){
        this.message = msg;
        this.pos = pos;
    }

}
