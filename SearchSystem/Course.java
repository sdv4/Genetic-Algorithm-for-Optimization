/**
*
* Course.java
*
* Class whose instances are represantative of course objects for the given search
* instance.
*
* @author Justina Lem
* @author Kevin Naval
* @author Chi Zhang
* @author Shane Sims
*
* @version 6 December 2017
*/
import java.util.concurrent.atomic.AtomicInteger;

public class Course {

    private static final AtomicInteger counter = new AtomicInteger(0);

    // Class attributes
    private final String name;
    private final int id;
    private final int lectureNumber;

    public Course(String name, int lectureNumber){
        this.id = counter.incrementAndGet();
        this.name = name;
        this.lectureNumber = lectureNumber;
    }

    public int getId(){
        return id;
    }

    public String name(){
        return name;
    }

    public int getLecNum(){
        return lectureNumber;
    }
}
