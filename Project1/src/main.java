/**
 *
 * @author Sulbha Aggarwal
 * Submitted to: Dr. Fluture for CS 340 Project 1
 * class: main.java
 * Note: for number of students, there are two options
 *       1. by default number of students are 13
 *       2. command line argument that will change nStudents to the
 *         argument passed in as integer
 *
 */

public class main
{
    //initialized restroom object object
    static restroom bathroom = new restroom();

    //initialized an 2 D array for taking attendance of the students
    static boolean classAttendance[][];

    //this can be changed by command line arguments
    static int nStudents = 3;

    //created student thread
    static Thread[] students  ;

    //boolean flag to check if a student have arrived the school
    public static  boolean studentArrived = false ;

    //boolean array to mark the students as being called
    public static boolean[] profCallin ;

    public static void main(String[] args)
    {
        long time = System.currentTimeMillis();

        //if args is greater than 0 then nStudents get that value
        if( args.length > 0 ) nStudents = Integer.parseInt(args[0]);

        //initialized students thread
        students  = new Student[nStudents] ;

        //initialized class Attendance 2D boolean array.
        classAttendance = new boolean[nStudents][5];

        //initialize boolean array
        profCallin = new boolean[nStudents] ;

        //initializing student threads
        for(int i =0; i < nStudents;i++){
            students[i] = new Student(i);
        }

        //Initialize Professor thread
        Thread teacher = new Professor("Dr. Fluture" ) ;

        while ( !Professor.done )
        {
            System.out.print( "" ) ;
        }

        System.out.println( "[" + (System.currentTimeMillis()-time)+"] " +
                teacher.getName() + " is in the campus? " + teacher.isAlive() ) ;

        System.out.println( "[" + (System.currentTimeMillis()-time)+"] Main:" + " School is now Closed" ) ;
        System.out.println( "\n-----------------------------------------------------------------***************-----------------------------------------------------------------" ) ;


    }
}
