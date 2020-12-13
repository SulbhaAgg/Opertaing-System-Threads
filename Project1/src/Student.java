import java.util.Random ;

/**
 *
 * @author Sulbha Aggarwal
 * Submitted to: Dr. Fluture for CS 340 Project 1
 * class: Student.java
 * Note: Made it easier to see the output by commmenting line 88, otherwise
 *       it takes alot of output space to print that student is waiting for prof to start the lecture
 *       The line could be uncommented if you want to see for how long student is
 *       waiting for Prof to start the lecture
 */

public class Student extends Thread
{
    //every student has an id
    private int id ;

    //Each student has a name
    private String name = "" ;

    //for random sleep time and randomizing students for priority
    private Random random ;
    private Thread thread ;

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
     * rushToClass(): sets priority for students who are eager to learn
     *                After the student has increased priority, she will
     *                sleep for random time and as soon as she wakes up
     *                the priority is set back to the normal
     */
    public void rushToClass()
    {
        int priority = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(priority + 1);
        randSleep();
        Thread.currentThread().setPriority(priority);
    }

    /**
     * attendClass(): There are 5 classes by the professor
     *                if the student arrives the class before Prof begins the session
     *                then get get marked for attendance first
     *                then students get bored during class and they fall asleep and prof wakes them up when the class is over
     *                else if they arrive after the class started
     *                then they cannot enter the classroom and have to walk around the
     *                campus
     *
     */
    public void attendClass()
    {
        if( Professor.period < 5 )
        {
            // students are Busy Waiting until session starts
            if( !Professor.isInSession() ) //  if class is not in session
            {
                //students wait for for teacher to arrive and enter the auditorium
                while ( !Professor.isInSession())
                {
//                    msg( "Entered the classroom and waiting for the Professor to start the Period" ) ;
                    Thread.currentThread().yield();
                    //busy wait until the class starts
                }

                // period 2 is prof office hours
                if (Professor.period != 2) //if  it is not prof office hours
                {
                    msg( "Prof have arrived for Period: " + Professor.period ) ;

                    //marking student present for the specific period
                    main.classAttendance[id][Professor.period] = true;

                    msg("In the class studying, period number: " + (Professor.period));

                    try
                    {
                        msg( "Got bored and fall asleep in period " + (Professor.period) ) ;
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException e)
                    {
                        msg("teacher interrupts the sleep for period " + (Professor.period) );

                        /*students took a little break after class, some will be back on time
                         for the next period or some might be late so will walk around*/
                        if (Professor.period != 4)
                        {  //because if it is the last period then they don't walk around, they go home
                            randSleep();
                            msg("took a break in between classes");
                        }
                    }
                } else //This is office hours
                {
                    msg( "Prof have arrived for Office hours" ) ;

                    //marking student present for the specific period
                    main.classAttendance[id][Professor.period] = true;

                    msg("In Professor Office Hours");
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e)
                    {
                        msg("teacher office hours are over and is being interrupted by Prof");

                        //students took a little break after office hours, some will be back on time
                        // for the next period or some will walk around
                        randSleep();
                        msg("took a break after Prof office hours");
                    }
                }
            }
            else //if the student is late to the class
            {
                if(Professor.period != 2)
                    msg("Walking around the campus because of lateness/ want to skip the Period " + (Professor.period ));
                else
                    msg("Walking around the campus because of lateness/ want to skip the Office hours" );
                try
                {
                    Thread.sleep(1500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
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
        while( !Professor.teacherCalled(this.getName()))
        {
            //busy waiting ;
        }
        msg("is called by the prof, is going to wash hands in the bathroom before entering the classroom") ;

        /*an boolean array to flag that student is called in by the prof to use bathroom
          because once Prof start teaching, it will be difficult to leave the classroom and
          take care of students entering the school and sending to use the bathroom
         */
        main.profCallin[id] = true ;

        //teacher is done calling this student now call next one
        Professor.teacherCalled = false ;


        //bathroom
        randSleep();
        msg("is going to the bathroom line");
        main.bathroom.addToTheLine(id);
        randSleep();

        boolean flag = false  ;
        if(!main.bathroom.useBathroom(id)){
            msg("is waiting in the line for a bathroom to be empty");

            //yield three times
            Thread.currentThread().yield();
            Thread.currentThread().yield();
            Thread.currentThread().yield();
        }
        else
        {
            //bathroom is empty and ready to be used by the student
            flag = true;
        }

        //while bathroom does not empty busy wait
        while( !flag && !main.bathroom.useBathroom(id ) )
        {
            System.out.print("");
            //busy wait
        }

        //once the bathroom is free student can use it
        msg("is going inside to use the bathroom");
        randSleep();
        msg("has finished using the bathroom");
        main.bathroom.exitBathroom(id);

        //random number generate to decide randomly students eager to learn
        int rand = random.nextInt();

        //if the random number is even, student is eager to learn will rush to the class else will walk
        if (rand % 2 == 0) {
            msg("is eager to learn new things and is rushing to class");
            rushToClass();
        }
        else
            msg("is not in a rush and is walking to class");

        //boolean flag to make sure at least one student is arrived before prof starts the class
        main.studentArrived = true ;

        // a while loop to go through 4 periods and 1 office hours and until school is not finished
        while( Professor.period < 5 && !Professor.schoolDone )
            attendClass() ;

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
