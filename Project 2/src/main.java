import java.util.concurrent.Semaphore;

/**
 *
 * @author Sulbha Aggarwal
 * Submitted to: Dr. Fluture for CS 340 Project 2
 * class: main.java
 * Note: for number of students, there are two options
 *       1. by default number of students are 13
 *       2. command line argument that will change nStudents to the
 *         argument passed in as integer
 *
 *       Also, Professor I did both Semaphore and join() implemenataion for students and teacher leaving the school
 *       Both are working fine
 *       I commented the join implementation. The join() could be uncommented if you want to see the join implementation.
 *       For that the semaphore implementation have to commented
 */

public class main
{
    //initialized restroom object object
    static restroom bathroom = new restroom();

    //initialized an 2D array for taking attendance of the students
    static boolean classAttendance[][];

    //this can be changed by command line arguments
    static int nStudents = 5;

    //created student thread
    static Thread[] students  ;

    //boolean flag to check if a student have arrived the school
    public static  boolean studentArrived = false ;

    /* if the students have already went to the class or have been late to the class
     * this is important because a student was attending a period twice otherwise
     */
    static boolean classGone[][];

    //Semaphore to indicate that student is waiting to be called by prof to use bathroom
    static Semaphore waitingToBeCalled = new Semaphore(0 ,  true) ;

    // Semaphore for girl's bathroom
    static Semaphore useGirlBathroom = new Semaphore(3 ,  true) ;

    //Semaphores for boy's bathroom
    static Semaphore useBoyBathroom = new Semaphore(3 ,  true) ;

    /* Semaphore to indicate that period 1 is finished and all the students have either attended or being late
     * This is important being when students were attending period 0, In the professor class,
     * it reaches period 1 already. So needed a block there
     */
    static Semaphore donePeriod0 = new Semaphore( 0 , true ) ;

    static long time = System.currentTimeMillis();

    //Semaphore to indicate that school is over
    static Semaphore schoolOver = new Semaphore( 0, true) ;

    //Semaphore to indicate students waiting to leave
    static Semaphore studentDone = new Semaphore( 0 , true ) ;


    public static void main(String[] args)
    {

        //if args is greater than 0 then nStudents get that value
        if( args.length > 0 ) nStudents = Integer.parseInt(args[0]);

        //initialized students thread
        students  = new Student[nStudents] ;

        //initialized class Attendance 2D boolean array.
        classAttendance = new boolean[nStudents][2];

        //initialized class Attendance 2D boolean array.
        classGone = new boolean[nStudents][2];

        //initializing student threads
        for(int i =0; i < nStudents;i++){
            students[i] = new Student(i);
        }

        //Initialize Professor thread
        Thread teacher = new Professor("Dr. Fluture" ) ;

        //Semaphore blocking until school is over
        try
        {
            schoolOver.acquire();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        //students sleep for .5 seconds, time took to pack up before leaving school
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Students leaving the school
        leaveSchool();

        /* The teacher will wait until the last student leaves and after that
         * she will terminate as well
         */
        //Semaphore implementation
        if( teacher.isAlive() )
        {
            try {
                studentDone.acquire() ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //join implementation
//        try
//        {
//            teacher.join();
//        }
//        catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }

        //Print statement showing that teacher have left the school
        System.out.println( "[" + (System.currentTimeMillis()-time)+"] " +
                teacher.getName() + " is in the campus? " + teacher.isAlive() ) ;

        System.out.println( "[" + (System.currentTimeMillis()-time)+"] Main:" + " School is now Closed" ) ;

        //Daily Report
        System.out.println( "\nDaily Report: " ) ;
        dailyReport() ;
        System.out.println( "\n-----------------------------------------------------------------***************-----------------------------------------------------------------" ) ;


    }

    /**
     * leaveSchool(): At the end of school day, students leave the school.
     *                Each student will wait another student; they will leave in decreasing
     *                order of their name or their ID.
     */
    //Semaphore implementation of students leaving
    private static void leaveSchool()
    {
            //student leaving in decreasing order
            for (int i = nStudents - 1; i > 0; i--)
            {
                if( students[i].isAlive() )
                {
                    try
                    {
                        //wait is done through p blocking on sem
                        studentDone.acquire();
                        studentDone.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                System.out.println("[" + (System.currentTimeMillis() - time) + "] Student- " + (i)
                        + " is still in the campus? " + students[i].isAlive() + ".");
            }
    }

    //join() implementation of students leaving
    //    private static void leaveSchool()
//    {
//        try
//        {
//            //student leaving in decreasing order
//            for (int i = main.nStudents - 1; i > 0; i--)
//            {
//                students[i].join();
//                System.out.println("[" + (System.currentTimeMillis() - time) + "] Student- " + (i)
//                        + " is still in the campus? " + students[i].isAlive() + ".");
//            }
//        }
//        catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
//    }

    /**
     * dailyReport(): A daily report with information about what classes and when each student
     *                attended throughout the day is displayed
     */
    private static void dailyReport()
    {
        String toString = "Student Id \t\t Total Attended Classes \t\t Class Name \t\t Periods Attended\n" ;

        for( int i = 0 ; i < main.nStudents ; i++ )
        {
            toString += main.students[i].getName() + " \t\t\t\t " ;
            int totalAttended = 0 ;
            String periodNum = "" ;
            String periodName = "" ;
            for( int j = 0 ; j < 2 ; j++ )
            {
                if( main.classAttendance[i][j] )
                {
                    totalAttended++ ;
                    periodName += Professor.className[j] + " "  ;
                    periodNum += j + " " ;
                }
            }
            toString += totalAttended + " \t\t\t\t\t\t" + periodName +" \t\t\t\t" + periodNum + "\n" ;
        }
        System.out.println( "\n" + toString );
    }

}
