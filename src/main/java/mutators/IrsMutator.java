package mutators;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import common.MutantLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class IrsMutator extends AMutator {

    HashMap<Node, DependencyPayload> rwdDependencies;


    public IrsMutator(MutantLog method){
        this(method, Integer.MAX_VALUE);
    }
    public IrsMutator(MutantLog method, int maxMutants) {
        super(method,maxMutants);

//        allBinaryExpr = new ArrayList<>();
//
//        getAllNodeOfClass(method.getMutant(), allBinaryExpr, BinaryExpr.class);
//
//        allBinaryExpr = allBinaryExpr.stream().filter(binaryExpr -> {
//            BinaryExpr.Operator op_type = binaryExpr.getOperator();
//            return ((op_type == BinaryExpr.Operator.PLUS) ||
//                    (op_type == BinaryExpr.Operator.MULTIPLY));
//        }).collect(Collectors.toList());

    }

    @Override
    public HashSet<MutantLog> getMutants(Set<MethodDeclaration> existingMethods) {
        return null;
    }
}

class DependencyPayload {
    private HashSet readVars;
    private HashSet writeVars;
    private HashSet declaredVars;

    public DependencyPayload(HashSet readVars, HashSet writeVars, HashSet declaredVars) {
        this.readVars = readVars;
        this.writeVars = writeVars;
        this.declaredVars = declaredVars;
    }

    public HashSet getReadVars() {
        return readVars;
    }

    public HashSet getWriteVars() {
        return writeVars;
    }

    public HashSet getDeclaredVars() {
        return declaredVars;
    }
}

class ReadWriteDependVisitor extends VoidVisitorAdapter<Void> {
    @Override
    public void visit(AnnotationDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(AnnotationMemberDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ArrayAccessExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ArrayCreationExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ArrayInitializerExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(AssertStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(AssignExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(BinaryExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(BlockComment n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(BlockStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(BooleanLiteralExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(BreakStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(CastExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(CatchClause n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(CharLiteralExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceType n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(CompilationUnit n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ConditionalExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ConstructorDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ContinueStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(DoStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(DoubleLiteralExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(EmptyStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(EnclosedExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(EnumConstantDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(EnumDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ExplicitConstructorInvocationStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ExpressionStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(FieldAccessExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ForEachStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ForStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(IfStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(InitializerDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(InstanceOfExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(IntegerLiteralExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(JavadocComment n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(LabeledStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(LineComment n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(LongLiteralExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(MarkerAnnotationExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(MemberValuePair n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodCallExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(NameExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(NormalAnnotationExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(NullLiteralExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ObjectCreationExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(PackageDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(Parameter n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(PrimitiveType n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(Name n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(SimpleName n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ArrayType n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ArrayCreationLevel n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(IntersectionType n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(UnionType n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ReturnStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(SingleMemberAnnotationExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(StringLiteralExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(SuperExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(SwitchEntryStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(SwitchStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(SynchronizedStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ThisExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ThrowStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(TryStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(LocalClassDeclarationStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(TypeParameter n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(UnaryExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(UnknownType n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclarator n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(VoidType n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(WhileStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(WildcardType n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(LambdaExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodReferenceExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(TypeExpr n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(NodeList n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ImportDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ModuleDeclaration n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ModuleRequiresDirective n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ModuleExportsDirective n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ModuleProvidesDirective n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ModuleUsesDirective n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ModuleOpensDirective n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(UnparsableStmt n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(ReceiverParameter n, Void arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(VarType n, Void arg) {
        super.visit(n, arg);
    }
}
