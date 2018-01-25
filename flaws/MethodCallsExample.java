package flaws; /**
 * Created by Vedant on 01-Dec-16.
 */

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MethodCallsExample {

    public static ArrayList<MethodCallExpr> listMethodCalls(File projectDir) {
        ArrayList<MethodCallExpr> list=new ArrayList<MethodCallExpr>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println(path);
            System.out.println(Strings.repeat("=", path.length()));

            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodCallExpr n, Object arg) {

                        super.visit(n, arg);

                        list.add(n);
                    }
                }.visit(JavaParser.parse(file), null);
                System.out.println(); // empty line
            } catch (ParseException | IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
        return list;
    }

    public static void main(String[] args) {
        File projectDir = new File("C:\\Users\\Vedant\\Desktop\\Test");
        ArrayList<MethodCallExpr> list=listMethodCalls(projectDir);
        for(MethodCallExpr mcr:list){
            System.out.println(" [L " + mcr.getBeginLine() + "] " + mcr);
        }
    }
}
