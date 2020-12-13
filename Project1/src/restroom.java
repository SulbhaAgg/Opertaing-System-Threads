import java.util.LinkedList;
/**
 *
 * @author Sulbha Aggarwal
 * Submitted to: Dr. Fluture for CS 340 Project 1
 * class: restroom.java
 *
 */

public class restroom
{

    //LinkedList for the line outside the girl's bathroom
    static LinkedList<Integer> girlBathroom ;

    //LinkedList for the line outside the boy's bathroom
    static LinkedList<Integer> boyBathroom ;

    //LinkedList for the students using girl's bathroom
    static LinkedList<Integer> girlLine ;

    //LinkedList for the students using boy's bathroom
    static LinkedList<Integer> boyLine ;

    /**
     * Constructor to intantiate the linkedLists
     */
    public restroom()
    {
        girlBathroom = new LinkedList<>() ;
        boyBathroom = new LinkedList<>() ;
        girlLine = new LinkedList<>() ;
        boyLine = new LinkedList<>() ;
    }

    /**
     * addToTheLine(): adds the student to their respective bathroom line
     * @param id passed it to determines the gender
     */
    public void addToTheLine( int id )
    {
        if( gender(id) )
            girlLine.add(id ) ;
        else if( !gender(id)  )
            boyLine.add( id ) ;
    }

    /**
     * useBathroom(): makes student use bathroom on first come first serve basis.
     *                The first student on line get to use the respective bathroom
     *                accordingly
     * @param id passed in to add the id to the bathroom list and remove from the waiting list
     * @return ture if the student is successfully able to use the bathroom
     */
    public Boolean useBathroom( int id )
    {
        if( gender(id) && girlBathroom.size() <= 2 &&  girlLine.get(0) == id  )
        {
            girlBathroom.add(id);
            girlLine.remove((Object)id);
        }
        else if( !gender(id) && boyBathroom.size() <= 2 &&  boyLine.get(0) == id)
        {
            boyBathroom.add(id);
            boyLine.remove((Object)id);
        }
        else
        {
            return false;
        }

        return true;

    }

    /**
     * exitBathroom(): when a student leave bathroom removes their object to make
     *                 room for the waiting students
     * @param id passed into remove the specific object
     */
    public void exitBathroom( int id )
    {
        if( gender(id) )
            girlBathroom.remove( (Object)id ) ;
        else if( !gender(id) )
            boyBathroom.remove( (Object)id ) ;
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
