package ast.def;

import ast.ASTVisitor;
import ast.SingleVarDefNode;
import util.position.Position;
import util.type.Type;

import java.util.ArrayList;

public class VarDefNode extends DefinitionNode {
    public Type type;
    public ArrayList<SingleVarDefNode> vars = new ArrayList<>();

    public VarDefNode(Position pos, Type type){
        super(pos);
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}


