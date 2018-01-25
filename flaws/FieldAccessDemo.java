package flaws; /**
 * Created by Vedant on 03-Dec-16.
 */

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ClassDataTest{
    ClassOrInterfaceDeclaration cd;
    String ClassName;
    ArrayList<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
    int incomingMethodCalls;
    int outgoingMethodCalls;
    //ArrayList<Integer> outgoingMethodCalls = new ArrayList<Integer>();
    ArrayList<String> fields = new ArrayList<String>();
    ArrayList<String> methodCalls = new ArrayList<String>();
    ArrayList<String> methodNames = new ArrayList<String>();
    int MethodFieldAccess;

    public ClassDataTest(ClassOrInterfaceDeclaration cd, String className, ArrayList<MethodDeclaration> methods, ArrayList<String> fields) {
        this.cd = cd;
        ClassName = className;
        this.methods = methods;
        this.fields = fields;
    }
    public void methodCallsSet(ArrayList<String> mc){
        for(String st: mc){
            methodCalls.add(st);
        }
    }
    void ComputeMethodNames(){
        for(MethodDeclaration md: methods){
            methodNames.add(md.getName());
        }
    }

    void ComputeFields() {
        //for(int i=0;i<fields.size();i++)
        //System.out.println(fields.get(i));

        for (int i=0;i<fields.size();i++){
            String str = fields.get(i);

            if (str.contains(",")) {
                String str1 = str.substring(str.indexOf(",")+1);
                str = str.substring(0,str.indexOf(","));
                str1=str1.trim();
                fields.remove(i);
                fields.add(str);
                fields.add(str1);
                i--;
            }
            if (str.contains("=")){
                str = str.substring(0,str.indexOf("="));
                //System.out.println(str);
                str= str.trim();
                fields.remove(i);
                fields.add(str);
            }
        }
    }
}

public class FieldAccessDemo {

    public static ArrayList<ClassDataTest> returnMemberData(ArrayList<String> pathList) throws IOException, ParseException {
        // creates an input stream for the file to be parsed

        ArrayList<ClassDataTest> classes = new ArrayList<ClassDataTest>();
        for (String path: pathList) {

            FileInputStream in = new FileInputStream(path);

            CompilationUnit cu;
            try {
                // parse the file
                cu = JavaParser.parse(in);
            } finally {
                in.close();
            }


            // visit and print the methods names
            ArrayList<ClassOrInterfaceDeclaration> classes1 = new ArrayList<ClassOrInterfaceDeclaration>();
            classes1 = new ClassVisitor().getClassName(cu, null);
            ArrayList<String> methods = new ArrayList<String>();

            for (ClassOrInterfaceDeclaration cd1 : classes1) {
                ClassDataTest cd = new ClassDataTest(cd1, cd1.getName(), new MethodVisitor().getMethods(cd1, cu, null), new FieldVisitor().getFields(cd1, cu, null));
                classes.add(cd);
                //System.out.println(cd.getName());
                //System.out.println();
            }

            for (ClassDataTest cd : classes) {
                cd.ComputeMethodNames();
                cd.ComputeFields();
               // System.out.println(cd.ClassName + " " + cd.methodNames + " " + cd.fields);
               // System.out.println("  ");
            }

            for (ClassDataTest cd : classes) {

                for (MethodDeclaration md1 : cd.methods) {
                    methods = new MethodCallVisitor().getMethodCalls(md1, cu, null, methods,cd.methodNames);

                }
                cd.methodCallsSet(methods);
                //System.out.println(methods);
                methods.clear();
                //methods = new FieldAccess().getFieldAccesses(classes.get(2).methods.get(1), cu, null);
            }

            for (ClassDataTest cd : classes) {
                cd.MethodFieldAccess = new FieldAccessList().computeMethodFieldAccess(cd.methods, cd.fields, cu, null);
                //System.out.println(cd.MethodFieldAccess);

            }


        }
        for (ClassDataTest cd : classes) {
            //System.out.println("Class " + cd.cd.getName());

            //System.out.println(cd.methodCalls);
            //System.out.println();
            cd.outgoingMethodCalls = cd.methodCalls.size();
            for (String str : cd.methodCalls) {
                for (ClassDataTest cd1 : classes) {
                    for (MethodDeclaration md : cd1.methods) {
                        //System.out.println(str+"-------"+md.getName());
                        if(!cd.ClassName.equals(cd1.ClassName))
                            if (str.equals(md.getName())) {
                                cd1.incomingMethodCalls++;
                            }
                    }
                }
            }
        }


        return classes;
    }

}

/**
 * Simple visitor implementation for visiting MethodDeclaration nodes.
 */
class ClassVisitor {

    public static ArrayList<ClassOrInterfaceDeclaration> getClassName(CompilationUnit cu, Object arg) {

        ArrayList<ClassOrInterfaceDeclaration> list = new ArrayList<ClassOrInterfaceDeclaration>();
        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                // here you can access the attributes of the method.
                // this method will be called for all methods in this
                // CompilationUnit, including inner class methods
                //System.out.println(n.getName());
                list.add(n);
                super.visit(n, arg);
            }
        }.visit(cu, arg);
        return list;
    }
}

class FieldVisitor{
    public static ArrayList<String> getFields(ClassOrInterfaceDeclaration cd,CompilationUnit cu,Object arg){
        ArrayList<String> list = new ArrayList<String>();
        new VoidVisitorAdapter<Object>(){
            @Override
            public void visit(FieldDeclaration n, Object arg){
                super.visit(n,arg);
                if(cd.contains(n)) {
                    String f1 = n.getVariables().toString().substring(1,n.getVariables().toString().length()-1);
                    //list.add(n.getVariables().toString());
                    list.add(f1);
                }
            }
        }.visit(cu, arg);

        return list;
    }
}

class MethodVisitor{
    public static ArrayList<MethodDeclaration> getMethods(ClassOrInterfaceDeclaration cd,CompilationUnit cu,Object arg){
        ArrayList<MethodDeclaration> list = new ArrayList<MethodDeclaration>();
        new VoidVisitorAdapter<Object>(){
            @Override
            public void visit(MethodDeclaration n, Object arg){
                super.visit(n,arg);
                if(cd.contains(n)) {
                    //System.out.println(n.toString());
                    list.add(n);
                }
            }
        }.visit(cu, arg);

        return list;
    }
}

class MethodCallVisitor{
    public static ArrayList<String> getMethodCalls(MethodDeclaration md,CompilationUnit cu,Object arg, ArrayList<String> list, ArrayList<String> methodNamesList){
        //ArrayList<String> list = new ArrayList<String>();
        new VoidVisitorAdapter<Object>(){
            @Override
            public void visit(MethodCallExpr n, Object arg){
                super.visit(n,arg);
                if(md.contains(n)) {
                    //System.out.println(n.getName());

                    boolean status=false;
                    for(String methodName:methodNamesList){
                        if(methodName.equals(n.getName()))
                            status=true;
                    }
                    if(!status) {
                        list.add(n.getName());
                        System.out.println(n.getName());
                    }
                }
            }
        }.visit(cu, arg);

        return list;
    }
}

class FieldAccessList{
    public static ArrayList<String> getFieldAccesses(MethodDeclaration md,CompilationUnit cu,Object arg){
        ArrayList<String> list = new ArrayList<String>();
        new VoidVisitorAdapter<Object>(){
            @Override
            public void visit(FieldAccessExpr n, Object arg){
                super.visit(n,arg);
              // System.out.println(n);
                if(md.contains(n)) {
                    //System.out.println(n.getParentNode());
                    list.add(n.getField());
                   // System.out.println(n+"------");
                }
            }
        }.visit(cu, arg);

        return list;
    }

    int computeMethodFieldAccess(ArrayList<MethodDeclaration> methods, ArrayList<String> fields,CompilationUnit cu, Object arg){
        int count=0;
        ArrayList<String> fi = new ArrayList<String>();
        for(MethodDeclaration md: methods){
            fi = new FieldAccessList().getFieldAccesses(md,cu,arg);

            if(fi.size()>0) {
                for (int i=0;i<fi.size();i++) {
                    String str=fi.get(0);
                    //System.out.println(fi);
                    if (!fields.contains(str)) {
                        fi.remove(i);
                        i--;
                    }
                }
            }
            count = count+fi.size();

            for(String str: fields){
                String method_body = md.toString().substring(md.toString().indexOf('{'),md.toString().indexOf('}'));
                CharSequence str1 = str;

                if(method_body.contains(str)){
                    if(!fi.contains(str)){
                        fi.add(str);
                        count++;
                    }

                }
            }

            fi.clear();
            /*  Need to add code for local variable in method with same name of field */
        }

        return count;
    }
    public static ArrayList<String> getAccessedField(MethodDeclaration md, ArrayList<FieldDeclaration> fields,CompilationUnit cu, Object arg){

        ArrayList<String> fi = new ArrayList<String>();
        //for(MethodDeclaration md: methods){
            fi = new FieldAccessList().getFieldAccesses(md,cu,arg);

            ArrayList<String> field1=new ArrayList<String>();

            for(FieldDeclaration fd: fields){

                List<VariableDeclarator> temp_vd=fd.getVariables();
                for (VariableDeclarator vd:temp_vd){
                    String str=vd.toString();
                    if(str.contains("="))
                        str=str.substring(0,str.indexOf("=")-1);
                    //System.out.println(str);
                    field1.add(str);
                    //System.out.println(vd.toString());
                }


            }
            if(fi.size()>0) {
                for (int i=0;i<fi.size();i++) {
                    String str=fi.get(0);
                    //System.out.println(fi);
                    if (!field1.contains(str)) {
                        fi.remove(i);
                        i--;
                    }
                }
            }

            for(String str: field1){
                String method_body = md.toString().substring(md.toString().indexOf('{'),md.toString().indexOf('}'));
                CharSequence str1 = str;

                if(method_body.contains(str)){
                    if(!fi.contains(str)){
                        fi.add(str);

                    }

                }
            }

            /*  Need to add code for local variable in method with same name of field */
        //}

        //System.out.println(fi.get(0));
        return fi;
    }
}