/**
 *
 * @author Sulbha Aggarwal
 * Submitted to: Dr. Fluture for CS 340 Project 1
 * class: Professor.java
 *
 */

public class Professor extends Thread
{
    //Name of the Prof
    String name ;
    public static long time = System.currentTimeMillis();

    //Boolean flag to indicate that school id done for the day
    public static boolean schoolDone = false ;

    //boolean flag to indicate when teacher called students to use the bathroom
    public static boolean teacherCalled = false;

    //variable to keep track of current period
    public static int period = 0 ;

    //boolean flag to check if the class is in Session
    public static boolean inSession = false;

    //boolean flag to indicate school is over
    public static boolean done = false ;


    /**
     * Constructor
     * @param name for sets the name, setter method
     */
    public Professor( String name )
    {
        this.name = name ;
        setName("Teacher- " + name );
        start();
    }

    /**
     * isSchoolDone: method to indicate id the school is done for the day
     * @return: true is school is finished else false
     */
    public static boolean isSchoolDone()
    {
        return schoolDone ;
    }

    /**
     * isInSession: method to indicate if te class is in session
     * @return true if the period/class is in session otherwise false
     */
    public static boolean isInSession()
    {
        return inSession ;
    }

    /**
     * teacherCalled: keep track of the student called by the Prof
     * @param name to print the name of the student in system.out.println
     * @return true if called by teacher else false
     */
    public static boolean teacherCalled( String name )
    {
        System.out.println("["+(System.currentTimeMillis()-time)+"] Teacher- Dr. Simina Fluture: " + " is calling " + name + "." );
        return true ;
    }

    /**
     * DoneProfCalling: checks if prof done calling all the students for bathroom
     *                  once the period starts Prof cannot let anyone in
     * @return true all the students are already being called else false
     */
    private boolean DoneProfCalling()
    {
        boolean check = false ;
        for( int i = 0 ; i < main.nStudents ; i++ )
            if( main.profCallin[i] == false )
                return false ;
        return true ;
    }

    @Override
    public void run()
    {

        /*busy wait until prof is done calling all the students to the bathroom
         *because once the period start teacher might not have time
         * Also, making sure that at least one of the student have arrived to campus
         * otherwise prof will be teaching empty class
         */
        while (!DoneProfCalling() && !main.studentArrived) {
            System.out.print("");
        }

        //keeping track of 4 lasses and one office hour
        for (int k = 0; k < 5; k++)
        {

            period = k;

            // Period 2 is Prof Office hours
            if(period == 2)
            {
                msg("is having Office hours" );
                //once prof starts the class inSession flag is set to true
                inSession = true;
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                msg ( "is going to take Attendance for Office Hours" ) ;
                //interrupt the students when office hours is over and quickly marks students present
                for (int i = 0; i < main.nStudents; i++)
                {
                    //checking if the student was present during office hours if so then interrupt them
                    if (main.classAttendance[i][period])
                        main.students[i].interrupt();
                }

                msg("Office hours are over");
                //if it is the last period then session does not becomes false because hen the school is over
                if (period != 4)
                    inSession = false;

            }
            //regular class
            else {
                msg("Starting the Period: " + period );
                //once prof starts the class inSession flag is set to true
                inSession = true;

                //Once the teacher start teaching, simulating it by using sleep for a fixed interval of time
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                msg ( "is going to take Attendance for Period " + period ) ;
                //interrupt the students when the period is over
                for (int i = 0; i < main.nStudents; i++)
                {
                    //checking if the student was present during the period if so then interrupt them
                    if (main.classAttendance[i][period])
                        main.students[i].interrupt();
                }

                msg("Period " + period + " is over" ) ;

                //if it is the last period then session does not becomes false because hen the school is over
                if (period != 4)
                    inSession = false;

            }
            main.studentArrived = false ;

            if (period != 4)
            {
                //Prof take a short break after one class to prepare for the next class
                msg("going to take a short break after class to prepare for the next class");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
                msg("School is OVER" ) ;


        }

        schoolDone = true;

        //busy wait until the very last student in decreasing order is not finished with the periods
        while ( main.students[(main.nStudents-1)].isAlive() )
        {
            System.out.print("");
        }

        System.out.println( "["+(System.currentTimeMillis()-time)+"] Student- " + (main.nStudents-1)
                + " is still int the campus? " + main.students[(main.nStudents-1)].isAlive() + ".");

        //student leaving in decreasing order
        for (int i = main.nStudents - 1; i > 0; i--)
        {
            try {
                System.out.println( "[" + (System.currentTimeMillis()-time) + "] " + main.students[i - 1].getName()
                        + " will join " + main.students[i].getName() + " to leave the school.");
                main.students[i - 1].join(); //so far so good
              System.out.println( "["+(System.currentTimeMillis()-time)+"] " +  main.students[i - 1].getName()
                + " is still in the campus? " + main.students[(main.nStudents-1)].isAlive() + ".");

            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        msg( "will join Student-0 to leave the school" ) ;

        dailyReport() ;

        //made to true when school is over
        done = true ;


    }

    private void msg (String m)
    {
        System.out.println( "[" + (System.currentTimeMillis()-time)+"] " + getName() + ": " + m + "." );
    }

    private void dailyReport()
    {
        String toString = "Student Id \t\t Total Attended Classes \t\t Periods Attended\n" ;

        for( int i = 0 ; i < main.nStudents ; i++ )
        {
            toString += main.students[i].getName() + " \t\t\t\t " ;
            int totalAttended = 0 ;
            String periodNum = "" ;
            for( int j = 0 ; j < 5; j++ )
            {
                if( main.classAttendance[i][j] )
                {
                    totalAttended++ ;
                    if( j == 2)
                        periodNum += "Office hours " ;
                    else
                        periodNum += j + " " ;
                }
            }
            toString += totalAttended + " \t\t\t\t\t\t\t" + periodNum + "\n" ;
        }
        System.out.println( "\n" + toString );
    }

}
