public class SBS{


//OUTLINE

// 1. Build starting population
// Let's try a starting pop of 5 for testng
    private int[][] getStartPop(int size){
        int[][] startPop = new int[size];

        Parser aParser = new Parser("deptinst2.txt");
        aParser.start();
        ArrayList<CourseLab> courseLabList = aParser.getCourseLabList();
        ArrayList<Slot> slotCList = aParser.getCourseSlotList();
        ArrayList<Slot> slotLList = aParser.getLabSlotList();


    }






} // End SBS class
