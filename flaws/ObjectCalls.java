package flaws;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vedant on 24-Mar-17.
 */
public class ObjectCalls{

    static public int giveLinks(CompilationUnit cu, Object arg){
        ArrayList<String> list= new ArrayList<String>();
        new VoidVisitorAdapter<Object>(){
            @Override
            public void visit(VariableDeclarationExpr n, Object arg) {
                String s=n.toString().substring(0,n.toString().indexOf(' '));

                if(!((s.equals("int")||s.equals("String")||s.equals("int")||s.equals("float")||s.equals("double")||s.equals("char")||s.equals("boolean")||s.equals("byte")||s.equals("short")||s.equals("long")||s.equals("Object")||s.equals("Integer"))))
                    list.add(s);

                super.visit(n, arg);
                // System.out.println(n.toString().substring(0,n.toString().indexOf(' ')));
            }
        }.visit(cu,arg);
        Set<String> s = new HashSet<String>(list);
        System.out.println("Elements of Set of String Type: "+s);
        System.out.println("Set length: "+s.size());
        return s.size();
    }

}
