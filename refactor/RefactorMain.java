package refactor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import flaws.MethodDeclarationExample;
import jdk.nashorn.internal.ir.BlockStatement;


import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vedant on 16-Dec-16.
 */
public class RefactorMain {
    int a;
    public static void main(String[] args) throws IOException, ParseException {
        FileInputStream in = new FileInputStream("C:\\Users\\Vedant\\Desktop\\BE Project\\Examples\\MoveField\\Account.java");

        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }
        ClassOrInterfaceDeclaration cid=new ClassOrInterfaceDeclaration();
        cid.setName("Veda");

        cid.setParentNode(cu);
        File projectDir=new File("C:\\Users\\Vedant\\Desktop\\BE Project\\Examples\\MoveField");
        ArrayList<MethodDeclaration> mdList=MethodDeclarationExample.getMethods(projectDir);

        MethodDeclaration md=mdList.get(0);
        BlockStmt bs1=md.getBody();
        //BlockStmt bs2 = ;
        MethodCallExpr mc1 = new MethodCallExpr(null,"hi");
        ExpressionStmt exp1=new ExpressionStmt(mc1);
        mc1.setParentNode(md);
        bs1.getStmts().add(1,exp1);

        md.setParentNode(cid);
        ClassOrInterfaceDeclaration ty = cid;

        //System.out.println(JavaParser.parseClassBodyDeclaration(cid.toString()));
        //System.out.println(cid.getChildrenNodes());
    }
}