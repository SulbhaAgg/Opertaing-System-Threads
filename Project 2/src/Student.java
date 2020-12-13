import java.util.Random ;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Sulbha Aggarwal
 * Submitted to: Dr. Fluture for CS 340 Project 2
 * class: Student.java
 *
 */

public class Student extends Thread
{
    //Each student has an id
    private int id ;

    //Each student has a name
    private String name = "" ;

    //for random sleep time
    private Random random ;

    public static long time = System.currentTimeMillis();


    /**
     * Constructor
     * @param id for initializing the variables and sets the name, setter method
     */
    public Student( int id )
    {
        this.id = id ;
        random = new Random() ;

        //for simplicity, kept students name same as their id
        setName("Student-" + id);
        start() ;
    }

    /**
     * randSleep(): for random sleep up to 8 seconds
     */
    public void randSleep()
    {
        try
        {
            //sleep of .8 seconds
            Thread.sleep((long)(Math.random() * 800)) ;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * attendClass(): There are 2 classes by the professor
     *                if the student arrives the class before Prof begins the session
     *                then get get marked for attendance first
     *                Once the class is in session, students will immediately get bored
     *                and cannot wait for the class to end.
     *                else if they arrive after the class started
     *                then they cannot enter the classroom and have to walk around the
     *                campus
     *
     */
    public void attendClass()
    {
        //rather than calling Professor.period over and over I just call it once and makes it equal to period variable
        int period = Professor.period ;

        /* If period is less than 2 because there are 2 period: Period 0 and period 1
         * and If the student have not attended or missed the lecture before
         */
        if( period < 2 && !main.classGone[id][period])
        {
            //student have missed or attended the lecture
            main.classGone[id][period] = true;

            if( !Professor.isInSession() ) //  if class is not in session
            {

                try {

                    /*because sometimes, student will get to period 1 quicker
                     * before Professor blocks and to avoid deadlock
                     * checking if donePeriod0 is waiting to be released
                     */
                    if( Professor.period == 1 && main.donePeriod0.getQueueLength() > 0 )
                        main.donePeriod0.release();

                    //students waiting outside the class until prof starts the class
                    Professor.waitingInSession.acquire();
                    //Once class is opened students enter one after other
                    Professor.waitingInSession.release();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                msg( "Period: " + period + " is started" ) ;

                //marking student present for the specific period
                main.classAttendance[id][period] = true;

                msg("In the class studying, period number: " + (period));

                try
                {
                    //blocking until Prof singles that period is over
                    msg( "Cannot wait for the Period: " + period + " to end" ) ;
                    Professor.waitToEnd.acquire();

                    //Students signal fellow student that period is over
                    Professor.waitToEnd.release();
                    msg( "Signals other student that the class is over" ) ;
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                //After period 1 student don't need to take a break, they can just go home
                if( period == 0 )
                {
                    msg("is going to take a break in between classes");
                    randSleep();
                    msg("is done taking a break after period: " + period);
                }
            }
            else //if the student is late to the class
            {
                try
                {
                    //sleep of 1.5 seconds
                    Thread.sleep((long)(Math.random() * 1500)) ;
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                msg("Walking around the campus because of lateness for the Period " + period );
            }
        }
    }

    @Override
    public void run()
    {
        randSleep();

        //sleep for the health Questionnaire
        msg("wakes up and is going to take Health Questionnaire");
        randSleep();

        // second sleep for communte to school
        msg("has completed the Questionnaire and is commuting to school");
        randSleep();

        // third sleep waiting to be called by the teacher
        msg("arrived to the school and waiting in the schoolyard to be called by the teacher");

        //Student waiting to be called by the professor for using the bathroom
        try {

            main.waitingToBeCalled.acquire();
            msg("is called by the prof, is going to wash hands in the bathroom before entering the classroom") ;
       }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        //Student going to use the bathroom
        main.bathroom.useBathroom(id) ;

        //boolean flag to make sure at least one student is arrived before prof starts the class
        main.studentArrived = true ;

        //until school is not over
        while( !Professor.schoolDone )
        {
            /*
             * When changes in the period in professor reflects in Professor.period in student
             * then release the done with period 0
             */
            if( Professor.period == 1 )
                main.donePeriod0.release();
            System.out.print( "" ) ;
            attendClass() ;
        }

        if( id == (main.nStudents-1) )
            main.studentDone.release();

    }

    /**
     * msg: prints with the time in milliseconds and similar looking easily
     *      readable messages
     * @param m for the message passed in
     */
    private void msg(String m)
    {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+ m + ".");
    }
}
