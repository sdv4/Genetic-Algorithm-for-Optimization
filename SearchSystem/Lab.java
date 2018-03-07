/**
*
* Lab.java
*
* Class instantiating objects representative of labs/tutorials for a lecture.
*
* @author Shane Sims
* @author Justina Lem
*
* @version 6 December 2017
*/
import java.util.concurrent.atomic.AtomicInteger;

public class Lab {

    private static final AtomicInteger counter = new AtomicInteger(0);

    // Class attributes
    private final String name;
    private final int id;
    private final int labNumber;
    private final Course associatedLecture;

    public Lab(String name, int labNumber, Course associatedLecture){
        this.id = counter.incrementAndGet();
        this.name = name;
        this.labNumber = labNumber;
        this.associatedLecture = associatedLecture;
    }

    public int getId(){
        return id;
    }

    public String name(){
        return name;
    }

    public int getLabNum(){
        return labNumber;
    }

    public int getAssociatedLecId(){
        return associatedLecture.getId();
    }

    public Course getAssociatedLecture() {
        return associatedLecture;
    }
}
