package refactor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import flaws.DirExplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vedant on 16-Dec-16.
 */
public class MoveField {
    public static ArrayList<FieldDeclaration> newNoda( CompilationUnit cu) {
        ArrayList<FieldDeclaration> fd=new ArrayList<FieldDeclaration>();

        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(FieldDeclaration n, Object arg) {
                super.visit(n, arg);




            }
        }.visit(cu, null);

        //System.out.println(); // empty line

        return fd;
    }
}
