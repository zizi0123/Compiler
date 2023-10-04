package backend.regAllocate;

import assembly.ASMBlock;
import assembly.ASMFunction;
import assembly.Instruction.*;
import assembly.operand.*;
import backend.RegManager;

import java.util.*;
import java.util.LinkedList;

public class RegAllocater {

    public static class Edge {
        public Reg u, v;

        public Edge(Reg u, Reg v) {
            this.u = u;
            this.v = v;
        }

    }

    static int K = 27; //available physical regs
    ASMFunction function;
    RegManager regManager;
    HashSet<Reg> preColored = new HashSet<>(); //physical regs
    HashSet<Reg> initial = new HashSet<>(); //tmp regs
    LinkedList<Reg> simplifyWorkList = new LinkedList<>(); //low degree node,non mv-related
    LinkedList<Reg> freezeWorkList = new LinkedList<>(); //low degree node,mv-related
    LinkedList<Reg> spillWorkList = new LinkedList<>(); //high degree node
    HashSet<Reg> spilledNodes = new HashSet<>(); //nodes marked for spilling during this round; initially empty
    HashSet<Reg> coalescedNodes = new HashSet<>(); //registers that have been coalesced; when u←v is coalesced, v is added to this set and u put back on some work-list (or vice versa)
    Stack<Reg> selectStack = new Stack<>();


    LinkedHashSet<Mv> coalescedMoves = new LinkedHashSet<>();
    LinkedHashSet<Mv> constrainedMoves = new LinkedHashSet<>();
    LinkedHashSet<Mv> frozenMoves = new LinkedHashSet<>();
    LinkedHashSet<Mv> workListMoves = new LinkedHashSet<>();
    LinkedHashSet<Mv> activeMoves = new LinkedHashSet<>();


    HashSet<Edge> adjSet = new HashSet<>();
    HashMap<Reg, HashSet<Reg>> adjList = new HashMap<>();
    HashMap<Reg, Integer> degree = new HashMap<>();
    HashMap<Reg, HashSet<Mv>> moveList = new HashMap<>();
    HashMap<Reg, Reg> alias = new HashMap<>();
    HashMap<Reg, PhysicalReg> color = new HashMap<>(); //reg2color


    HashMap<VirtualReg, StackVal> vreg2Stack = new HashMap<>();
    ASMBlock currentBlock;


    public RegAllocater(ASMFunction function, RegManager regManager) {
        this.function = function;
        this.regManager = regManager;
    }

    public void allocate() {
        Init(function);
        LivenessAnalyzer livenessAnalyzer = new LivenessAnalyzer(function);
        livenessAnalyzer.work();
        Build();
        MakeWorkList();
        while (!simplifyWorkList.isEmpty() || !workListMoves.isEmpty() || !freezeWorkList.isEmpty() || !spillWorkList.isEmpty()) {
            if (!simplifyWorkList.isEmpty()) {
                Simplify();
            } else if (!workListMoves.isEmpty()) {
                Coalesce();
            } else if (!freezeWorkList.isEmpty()) {
                Freeze();
            } else {
                SelectSpill();
            }
        }
        AssignColor();
        if (!spilledNodes.isEmpty()) {
            RewriteFunction(spilledNodes);
            allocate();
        } else {
            replaceRegs();
        }
    }


    void Init(ASMFunction func) {
        initial.clear();
        simplifyWorkList.clear();
        freezeWorkList.clear();
        spillWorkList.clear();
        spilledNodes.clear();
        coalescedNodes.clear();
        selectStack.clear();

        coalescedMoves.clear();
        constrainedMoves.clear();
        frozenMoves.clear();
        workListMoves.clear();
        activeMoves.clear();

        adjSet.clear();
        adjList.clear();
        degree.clear();
        moveList.clear();
        alias.clear();
        color.clear();

        for (int i = 0; i < 31; ++i) {
            var reg = RegManager.regs[i];
            preColored.add(reg);
            adjList.put(reg, new HashSet<>());
            degree.put(reg, Integer.MAX_VALUE);
            moveList.put(reg, new HashSet<>());
            color.put(reg, reg);
        }

        for (var reg : regManager.virtualRegs) {
            initial.add(reg);
            adjList.put(reg, new HashSet<>());
            degree.put(reg, 0);
            moveList.put(reg, new HashSet<>());
        }
        //compute spill weight
        for (var block : func.blocks) {
            ArrayList<ASMIns> inses = new ArrayList<>(block.instructions);
            inses.addAll(block.exitInses);
            double weight = Math.pow(10, block.loopDepth);
            for (var inst : inses) {
                var reg = inst.getDef();
                if (reg != null) {
                    reg.useDefTime += weight;
                }
                for (var ureg : inst.getUse())
                    ureg.useDefTime += weight;
            }
        }
    }

    void addEdge(Reg u, Reg v) {
        if (u.equals(v)) return;
        Edge edge1 = new Edge(u, v);
        Edge edge2 = new Edge(v, u);
        if (!adjSet.contains(edge1)) {
            adjSet.add(edge1);
            adjSet.add(edge2);
            if (!preColored.contains(u)) {
                adjList.get(u).add(v);
                int deg = degree.get(u) + 1;
                degree.put(u, deg);
            }
            if (!preColored.contains(v)) {
                adjList.get(v).add(u);
                int deg = degree.get(v) + 1;
                degree.put(v, deg);
            }
        }

    }


    void Build() { //build interference graph, count node degree, collect mv ins
        for (var b : function.blocks) {
            HashSet<Reg> live = new HashSet<>(b.out);
            ArrayList<ASMIns> inses = new ArrayList<>(b.instructions);
            inses.addAll(b.exitInses);
            for (int i = inses.size() - 1; i >= 0; i--) {
                ASMIns I = inses.get(i);
                if (I instanceof Mv mv) {
                    live.remove(mv.rs);
                    moveList.get(mv.rs).add(mv);
                    moveList.get(mv.rd).add(mv);
                    workListMoves.add(mv);
                }
                var def = I.getDef();
                if (def != null) {
                    for (var l : live) {
                        addEdge(def, l);
                    }
                    live.remove(def);
                }
                live.addAll(I.getUse());
            }
        }
    }

    void MakeWorkList() {
        for (var r : initial) {
            if (degree.get(r) >= K) {
                spillWorkList.add(r);
            } else {
                if (moveRelated(r)) {
                    freezeWorkList.add(r);
                } else {
                    simplifyWorkList.add(r);
                }
            }
        }
    }

    void Simplify() {
        Reg r = simplifyWorkList.removeFirst();
        selectStack.push(r);
        HashSet<Reg> adj = adjacent(r);
        for (var a : adj) {
            decrementDegree(a);
        }
    }

    void decrementDegree(Reg r) {
        int d = degree.get(r);
        degree.put(r, d - 1);
        if (d == K) {
            HashSet<Reg> tmp = new HashSet<>(adjacent(r));
            tmp.add(r);
            enableMoves(tmp);
            spillWorkList.remove(r);
            if (moveRelated(r)) {
                freezeWorkList.add(r);
            } else {
                simplifyWorkList.add(r);
            }
        }
    }

    HashSet<Reg> adjacent(Reg r) {
        HashSet<Reg> adj = new HashSet<>(adjList.get(r));
        selectStack.forEach(adj::remove);
        adj.removeAll(coalescedNodes);
        return adj;
    }

    //与结点n相关且还未被处理的mv指令
    HashSet<Mv> nodeMoves(Reg n) {
        HashSet<Mv> nm = new HashSet<>(moveList.get(n));
        HashSet<Mv> tmp = new HashSet<>(activeMoves);
        tmp.addAll(workListMoves);
        nm.retainAll(tmp);
        return nm;
    }

    boolean moveRelated(Reg n) {
        return !nodeMoves(n).isEmpty();
    }

    void enableMoves(HashSet<Reg> nodes) { //将与nodes中每个节点关联的未处理的mv指令加入workListMoves;
        for (var node : nodes) {
            for (var m : nodeMoves(node)) {
                if (activeMoves.contains(m)) {
                    activeMoves.remove(m);
                    workListMoves.add(m);
                }
            }
        }
    }

    void addSimplifyList(Reg r) { //在coalesce中被处理过的节点，如果满足要求，将其加入simplifyWorkList中
        if (!preColored.contains(r) && !moveRelated(r) && degree.get(r) < K) {
            freezeWorkList.remove(r);
            simplifyWorkList.add(r);
        }
    }

    boolean George(Reg u, Reg v) {
        for (var adj : adjacent(v)) {
            if (!(degree.get(adj) < K || preColored.contains(adj) || adjSet.contains(new Edge(adj, u)))) return false;
        }
        return true;
    }

    boolean Briggs(Reg u, Reg v) {
        HashSet<Reg> union = new HashSet<Reg>(adjacent(u));
        union.addAll(adjacent(v));
        int num = 0;
        for (var r : union) {
            if (degree.get(r) >= K) ++num;
        }
        return num < K;
    }

    void combine(Reg u, Reg v) { //消去v
        if (freezeWorkList.contains(v)) {
            freezeWorkList.remove(v);
        } else {
            spillWorkList.remove(v);
        }
        coalescedNodes.add(v);
        alias.put(v, u);
        moveList.get(u).addAll(moveList.get(v));
        HashSet<Reg> tmp = new HashSet<>();
        tmp.add(v);
        enableMoves(tmp);
        for (var adj : adjacent(v)) {
            addEdge(adj, u);
            decrementDegree(adj); //删去和v的边.v的adj节点的度数可能会-1或不变，因此spillworkList内的node的度数可能并不>=K,但确保了freeze&simplify中的deg一定<K.
        }
        if (degree.get(u) >= K && freezeWorkList.contains(u)) { //u不会在simplify中，因为mv-related
            freezeWorkList.remove(u);
            spillWorkList.add(u);
        }
    }

    void Coalesce() {
        Mv mv = workListMoves.iterator().next();
        Edge edge;
        if (preColored.contains(mv.rs)) { //尽量满足v不是preColored reg
            edge = new Edge(mv.rs, mv.rd);
        } else {
            edge = new Edge(mv.rd, mv.rs);
        }
        workListMoves.remove(mv);
        Reg u = edge.u, v = edge.v;
        if (u == v) {
            coalescedMoves.add(mv);
            addSimplifyList(u);
        } else if (preColored.contains(v) || adjSet.contains(edge) || u.equals(regManager.getPReg("zero"))) {//zero寄存器只能储存0，不可以被合并
            constrainedMoves.add(mv);
            addSimplifyList(u);
            addSimplifyList(v);
        } else {
            //v 不是 preColored reg
            if ((preColored.contains(u) && George(u, v)) || (!preColored.contains(u) && Briggs(u, v))) {//将v消去
                coalescedMoves.add(mv);
                combine(u, v);
                addSimplifyList(u);
            } else { //在本次coalesce中还不满足conservative mv标准的要求，无法被合并的mv指令
                activeMoves.add(mv);
            }
        }
    }

    Reg getAlias(Reg r) { //disjoint set
        if (coalescedNodes.contains(r)) {
            return getAlias(alias.get(r));
        }
        return r;
    }

    void freezeMoves(Reg u) { //将与u相关的mv指令全部冻结
        for (var mv : nodeMoves(u)) {
            Reg v;
            if (getAlias(u) == getAlias(mv.rs)) {
                v = getAlias(mv.rd);
            } else {
                v = getAlias(mv.rs);
            }
            activeMoves.remove(mv); //workListMoves已空
            frozenMoves.add(mv);
            if (!moveRelated(v) && degree.get(v) < K) {
                freezeWorkList.remove(v);
                simplifyWorkList.add(v);
            }
        }
    }


    void Freeze() { //freeze:把一个freezeWorkList中的点相关的MV全部删掉，把它放入simplifyWorkList；把符合标准的和它有MV关联的节点也放入simplifyWorkList
        Reg u = freezeWorkList.removeFirst();
        simplifyWorkList.add(u);
        freezeMoves(u);
    }

    void SelectSpill() { //按照spillWeight由小到大把spill节点加入simplifyWL,即按序把它们从图上删去，放入栈中。
        Reg target = null;
        double spillWeight = 0;
        for (var reg : spillWorkList) {
            if (target == null || (reg.useDefTime / (double) degree.get(reg) < spillWeight)) {
                target = reg;
                spillWeight = reg.useDefTime / (double) degree.get(reg);
            }
        }
        spillWorkList.remove(target);
        simplifyWorkList.add(target);
        freezeMoves(target);
    }

    void AssignColor() {
        while (!selectStack.isEmpty()) {
            Reg r = selectStack.pop();
            HashSet<PhysicalReg> availableColor = new HashSet<>(List.of(RegManager.regs));
            for (var adj : adjList.get(r)) {
                Reg adjColor = color.get(getAlias(adj));
                if (adjColor != null) {
                    availableColor.remove(adjColor);
                }
            }
            if (availableColor.isEmpty()) {
                spilledNodes.add(r);
            } else {
                PhysicalReg chosenColor = availableColor.iterator().next();
                color.put(r, chosenColor);
            }
        }
        for (var r : coalescedNodes) {
            color.put(r, color.get(getAlias(r)));
        }
    }

    StackVal getStack(VirtualReg vreg) {
        if (vreg2Stack.containsKey(vreg)) {
            return vreg2Stack.get(vreg);
        }
        StackVal stackVal = new StackVal();
        function.stack.add(stackVal);
        vreg2Stack.put(vreg, stackVal);
        return stackVal;
    }

    Reg load(VirtualReg vreg) { //把值从vreg对应的栈中load出来，放入临时寄存器rd
        StackVal stackVal = getStack(vreg);
        Reg rd = regManager.getNewVirtualReg();
        Reg rs = regManager.getPReg("sp");
        if (vreg.size == 1) {
            currentBlock.addIns(new Lb(rd, rs, new StackOffset(stackVal), "load in" + rd + "\n"));
        } else {
            currentBlock.addIns(new Lw(rd, rs, new StackOffset(stackVal), "load in" + rd + "\n"));
        }
        return rd;
    }

    Reg store(VirtualReg vreg) { //把rs中的值放入vreg对应的栈中
        Reg rs = regManager.getNewVirtualReg();
        Reg rd = regManager.getPReg("sp");
        StackVal stackVal = getStack(vreg);
        if (vreg.size == 1) {
            currentBlock.addIns(new Sb(rs, rd, new StackOffset(stackVal), "store from" + rs + "\n"));
        } else {
            currentBlock.addIns(new Sw(rs, rd, new StackOffset(stackVal), "store from" + rs + "\n"));
        }
        return rs;
    }

    void RewriteFunction(HashSet<Reg> spilledNodes) {
        for (var vreg : spilledNodes) {
            getStack((VirtualReg) vreg); //allocate a stack space
        }
        for (var block : function.blocks) {
            currentBlock = block;
            ArrayList<ASMIns> newInstructions = new ArrayList<>();
            newInstructions.addAll(block.instructions);
            if (!block.exitInses.isEmpty()) {
                newInstructions.addAll(block.exitInses);
            }
            block.instructions.clear();
            block.exitInses.clear();
            for (var ins : newInstructions) {
                for (var use : ins.getUse()) {
                    if (spilledNodes.contains(use)) {
                        ins.replace(use, load((VirtualReg) use));
                    }
                }
                currentBlock.instructions.add(ins);
                var def = ins.getDef();
                if (def != null && spilledNodes.contains(def)) {
                    ins.replace(def, store((VirtualReg) def));
                }
            }
        }
    }

    void replaceRegs() {
        for (var block : function.blocks) {
            for (var ins : block.instructions) {
                for (var use : ins.getUse()) {
                    ins.replace(use, color.get(use));
                }
                var def = ins.getDef();
                if (def != null) {
                    ins.replace(def, color.get(def));
                }
            }
            var inses = new ArrayList<>(block.instructions);
            block.instructions.clear();
            for(var ins:inses){
                if(!(ins instanceof Mv mv && (mv.rd == mv.rs))){
                    block.instructions.add(ins);
                }
            }
            for (var ins : block.exitInses) {
                for (var use : ins.getUse()) {
                    ins.replace(use, color.get(use));
                }
                var def = ins.getDef();
                if (def != null) {
                    ins.replace(def, color.get(def));
                }
            }
            inses = new ArrayList<>(block.exitInses);
            block.exitInses.clear();
            for(var ins:inses){
                if(!(ins instanceof Mv mv && (mv.rd == mv.rs))){
                    block.exitInses.add(ins);
                }
            }
        }
    }




}
