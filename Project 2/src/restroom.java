import java.util.LinkedList;
/**
 *
 * @author Sulbha Aggarwal
 * Submitted to: Dr. Fluture for CS 340 Project 2
 * class: restroom.java
 *
 */

public class restroom
{
    public static long time = System.currentTimeMillis();


    /**
     * useBathroom(): makes student use bathroom on first come first serve basis.
     *                The first student on line get to use the respective bathroom
     *                accordingly
     *                There are two restrooms, one for “girls” and one for “boys.”
     *                The capacity of the restroom is three.
     *                Students will wait their turn to use the restrooms.
     * @param id passed in to add the id to indicate which student is waiting to use the bathroom,
     *           is using the bathroom and done using the bathroom
     */
    public void useBathroom( int id )
    {
        if( gender(id) )
        {
            try
            {
                System.out.println("["+(System.currentTimeMillis()-time)+"] Student-" + id + ": is going to use the Bathroom" + ".");
                main.useGirlBathroom.acquire();

                try
                {
                    //sleep of .8 seconds, time took to use the bathroom
                    Thread.sleep((long)(Math.random() * 800)) ;
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                main.useGirlBathroom.release();
                System.out.println( "["+(System.currentTimeMillis()-time)+"] Student-" + id + ": is done using the bathroom" );
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        else if( !gender(id) )
        {
            try
            {
                System.out.println("["+(System.currentTimeMillis()-time)+"] Student-" + id + ": is going to use the Bathroom" + ".");
                main.useBoyBathroom.acquire();
                try
                {
                    //sleep of .8 seconds, time taken to use the bathroom
                    Thread.sleep((long)(Math.random() * 800)) ;
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                main.useBoyBathroom.release();
                System.out.println( "["+(System.currentTimeMillis()-time)+"] Student-" + id + ": is done using the bathroom." );
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }


    }

    /**
     * gender(): the students with even id is girl otherwise boy
     * @param id to determine  the gender
     * @return true id even (girl) otherwise false
     */
    private boolean gender(int id)
    {
        return( id % 2 == 0 ) ;
    }


}
