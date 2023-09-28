package ast.def;

import ast.*;
import ast.util.position.Position;

import java.util.HashMap;

public class ClassDefNode extends DefinitionNode {

    public String className;

    public HashMap<String,SingleVarDefNode> members = new HashMap<>();

    public HashMap<String,FuncDefNode> functions = new HashMap<>();

    public ClassConstructorNode constructor;


    public ClassDefNode(Position pos, String name){
        super(pos);
        this.className = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
