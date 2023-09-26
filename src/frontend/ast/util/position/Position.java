package frontend.ast.util.position;


import org.antlr.v4.runtime.ParserRuleContext;

public class Position{
    private final int row;
    private final int column;

    public Position(int row,int column){
        this.row = row;
        this.column = column;
    }

    public Position(){
        this.row = 0;
        this.column = 0;
    }

//    public Position(Token token){
//        this.row = token.getLine();
//        this.column = token.getCharPositionInLine();
//    }

    public Position(org.antlr.v4.runtime.Token token) {
        this.row = token.getLine();
        this.column = token.getCharPositionInLine();
    }

    public Position(ParserRuleContext ctx){
        this(ctx.getStart());
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return column;
    }
}