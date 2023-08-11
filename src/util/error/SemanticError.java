package util.error;

public class SemanticError extends RuntimeException{
    public String message;

    public SemanticError(String msg){
        this.message = msg;
    }
}
