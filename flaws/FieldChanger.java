package flaws;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Created by Vedant on 15-Dec-16.
 */
class FieldChanger{
    public static void changeField(String field, String newField, CompilationUnit cu, Object arg){
        new VoidVisitorAdapter<Object>(){
            @Override
            public void visit(FieldDeclaration n, Object arg){
                super.visit(n,arg);
                if(n.getVariables().get(0).getId().toString().equals(field)) {
                    //System.out.println(n);
                    //List<VariableDeclarator> a = n.getVariables();
                    VariableDeclarator a1 = new VariableDeclarator();
                    a1 = n.getVariables().get(0);
                    a1.getId().setName(newField);
                    changeFieldAccess(field, newField, cu, arg);
                    //changeFieldDecl(field,newField,cu,arg);
                }
            }
        }.visit(cu, arg);

    }

    public static void changeFieldAccess(String field, String newField, CompilationUnit cu, Object arg){
        new VoidVisitorAdapter<Object>(){
            @Override
            public void visit(FieldAccessExpr n, Object arg){
                super.visit(n,arg);
                System.out.println(n.getField()+"------------"+field+n.getBegin());
                if(n.getField().equals(field)) {
                    //System.out.println(n);

                    //List<VariableDeclarator> a = n.getVariables();
                    n.setField(newField);
                }
            }
        }.visit(cu, arg);
    }

}
