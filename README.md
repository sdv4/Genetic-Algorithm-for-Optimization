# Agenetic Algorithm for Optimization
This algorithm implements a genetic algorithm used to solve a scheduling problem as it pertains to a computer science department's scheduling of courses, tutorials, and labs to time slots. In particular it finds an optimal solution to a specific scheduling problem, as defined by the following hard and soft constraints:

 Hard constraints:

    Not more than coursemax(s) courses can be assigned to slot s.
    Not more than labmax(s) labs can be assigned to slot s.
    assign(ci) has to be unequal to assign(lik) for all k and i.
    The input for your system will contain a list of not-compatible(a,b) statements, with a,b in Courses + Labs. For each of those, assign(a) has to be unequal to assign(b).
    The input for your system can contain a partial assignment partassign: Courses + Labs -> Slots + {$}. The assignment assign your system produces has to fulfill the condition:
    assign(a) = partassign(a) for all a in Courses + Labs with partassign(a) not equal to $.
    The input for your system can contain a list of unwanted(a,s) statements, with a in Courses + Labs and s in Slots. For each of those, assign(a) has to be unequal to s.
    There will be additional hard constraints specific to our Department that will be explained later. 

Soft constraints:

    Since there are usually time slots that are less liked than others, there is a certain pressure to also put courses and labs into the more unwanted slots. To facilitate this pressure, we have for each slot s minimal numbers coursemin(s) and labmin(s) that indicate how many courses, resp. labs, should at least be scheduled into the slot s. Your system should be able to accept as input penalty values pen_coursemin and pen_labsmin (as system parameters) and for each course below coursemin we will get pen_coursemin and for each lab pen_labsmin added to the Eval-value of an assignment.
    Certain professors that often teach certain courses have certain preferences regarding in which time slots their courses and labs should be scheduled. Naturally, we see this as something that should be treated as soft constraint. Depending on a to-be-determined ranking scheme, each professor will be awarded a certain set of ranking points and he/she can distribute these points over pairs of (course/lab, time slots). Formally, we assume a function preference: (Courses + Labs) x Slots -> Natural numbers that reports those preferences.
    For each assignment in assign, we add up the preference-values for a course/lab that refer to a different slot as the penalty that is added to the Eval-value of assign.
    For certain courses and/or labs, a department might know that there are never any students that take these courses/labs in the same semester. And therefore the department might find it convenient to have such courses/labs scheduled at the same time (this can also be used to keep students from taking certain courses prematurely). To facilitate this, there will be a list of pair(a,b) statements in the input for your system, with a,b in Courses + Labs, and a parameter pen_notpaired for your system. For every pair(a,b) statement, for which assign(a) is not equal to assign(b), you have to add pen_notpaired to the Eval-value of assign. 
    
    
 The system in this repository consists of my solution to this problem, and was implemented as a term project in C_P_S_C 4_3_3, where the above problem description was given (and thus not defined by me). 
 
 See GAForScheduling.pdf for a high level overview of the solution architecture.
