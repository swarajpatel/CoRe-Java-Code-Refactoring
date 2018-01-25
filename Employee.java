/**
 * Created by Vedant on 15-Dec-16.
 */
class Employee {
    private int id;
    private String name;

    public Employee(){}

    public void setId(int id){this.id=id;}

    public int getId(){return id;}

    public void setName(String name){this.name=name;}

    public String getName(){return name;}
    void m1(){
        getName();
    }
}  