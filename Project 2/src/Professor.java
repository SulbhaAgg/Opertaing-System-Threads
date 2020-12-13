import java.util.concurrent.Semaphore;

/**
 *
 * @author Sulbha Aggarwal
 * Submitted to: Dr. Fluture for CS 340 Project 2
 * class: Professor.java
 *
 */

public class Professor extends Thread
{
    //Name of the Prof
    String name ;

    public static long time = System.currentTimeMillis();

    //Boolean flag to indicate that school is done for the day
    public static boolean schoolDone = false ;

    //boolean flag to indicate when teacher called students to use the bathroom
    public static boolean teacherCalled = false;

    //variable to keep track of current period
    public static int period = 0 ;

    //boolean flag to check if the class is in Session
    public static boolean inSession = false;

    //waiting for professor to start the class
    static Semaphore waitingInSession = new Semaphore( 0 ,  true) ;

    //waiting for professor to end the class
    static Semaphore waitToEnd = new Semaphore( 0 ,  true) ;

    //created an array for the name of the classes
    static String[] className = new String[]{ "CS 340" , "CS 344" } ;

    /**
     * Constructor
     * @param name for sets the name, setter method
     */
    public Professor( String name )
    {
        this.name = name ;
        setName("Teacher- " + name );
        start();

        //set the priority to print Professor messages first
        setPriority(8);
    }

    /**
     * isInSession: method to indicate if te class is in session
     * @return true if the period/class is in session otherwise false
     */
    public static boolean isInSession()
    {
        return inSession ;
    }

    @Override
    public void run()
    {
        //sleep for 1.5 secs, prof waits for most of the students to arrive to the school
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
         * Semaphore to release the students waiting in the Queue to be called by
         * teacher then go and use bathroom
         */
        while( main.waitingToBeCalled.getQueueLength() > 0 )
                   main.waitingToBeCalled.release();

        //sleep for 1 secs, prof waits for most of the students to done using the bathroom before starting the first Period
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //keeping track of 2 classes
        for( int k = 0 ; k < 2 ; k++ )
        {
            period = k ;

            //block until students are finished wih period 0 and get to period 1
            if( k == 1 )
            {
                try
                {
                    main.donePeriod0.acquire();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            //once prof starts the class inSession flag is set to true
            inSession = true;
            msg("Starting the Period: " + period );

            //professor has started the class and signaled the students( let students in inside the class )
            waitingInSession.release() ;


            //Each class takes a fixed amount of the time period i.e. 2 seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            msg ( "is going to take Attendance for Period " + period ) ;

            msg( "Signaled that Period: " + period + " is over" ) ;
            //period ends
            waitToEnd.release();

            //if it is the last period then session does not becomes false because then the school is over
            if (period != 1 )
                inSession = false;

            main.studentArrived = false ;

            if (period != 1)
            {
                //Prof take a short break after one class to prepare for the next class
                msg("going to take a short break after class to prepare for the next class");
                try
                {
                    Thread.sleep(700);
                    waitingInSession.acquire();
                    waitToEnd.acquire();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                //sleep of .4 seconds because once 2nd period is over, prof quickly taking time to pack the stuff
                msg( "is packing for school to be over" ) ;
                try
                {
                    Thread.sleep(400);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                msg("Announces school is OVER");

            }

        }

        schoolDone = true;
        //Signal that school is over
        main.schoolOver.release() ;
    }

    /**
     * msg: prints with the time in milliseconds and similar looking easily
     *      readable messages
     * @param m for the message passed in
     */
    private void msg (String m)
    {
        System.out.println( "[" + (System.currentTimeMillis()-time)+"] " + getName() + ": " + m + "." );
    }

}
