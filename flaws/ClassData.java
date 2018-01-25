package flaws;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;

/**
 * Created by Vedant on 07-Dec-16.
 */
public class ClassData{
    public ClassOrInterfaceDeclaration cd;
    public String packageName,fileName;
    int number_of_fields;
    int number_of_methods;
    public ArrayList<FieldDeclaration> fieldDeclarations;
    public ArrayList<MethodDeclaration> methodDeclarations;
    int distinct_field_accesses=0;
    int incoming_calls=0,outgoing_calls=0;
    public int reportFlag[]=new int[7];
    public ClassData(){

    }
    public ClassData(ClassOrInterfaceDeclaration cd, int number_of_fields, int number_of_methods, ArrayList<FieldDeclaration> fieldDeclarations, ArrayList<MethodDeclaration> methodDeclarations) {
        this.cd = cd;
        this.number_of_fields = number_of_fields;
        this.number_of_methods = number_of_methods;
        this.fieldDeclarations = fieldDeclarations;
        this.methodDeclarations = methodDeclarations;
    }
}