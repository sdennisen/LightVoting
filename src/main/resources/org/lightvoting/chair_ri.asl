// initial fill level is 0, capacity is 3, can be any other value defined by config file
fill(0, 5).
// initial number of submitted diss vals

wait/time/vote(0, 10).

// instead of 10 it can be any other (random) value
//max/time/vote(10).

wait/time/diss(0, 10).
//max/time/diss(10).
started(0).

// initial iteration
iteration(0).

!start.

// as soon as group is opened, wait for votes
+!start
    : >>wait/time/vote(0, T)
    <-
       generic/print("Test Chair, Timeout ", T);
       !timedout/votes
        // !nextcycle
    .

+!timedout/votes
    : >>(wait/time/vote(X,Y), X < Y) && >>( fill(F, C), F < C )
    <-
        NewX = X+1;
        generic/print( "don't start election:", "time" , X, "fill", F );
        -wait/time/vote(X,Y);
        +wait/time/vote(NewX,Y);
        !timedout/votes
    : >>(wait/time/vote(X,Y), Y == X) &&  >>fill(F, C)
    <-
        generic/print( "start election:","time" , X , "fill", F );
        // close/group();
        // !clean/up/vote
        !started/voting(F)
    .

// store received vote in Java datastructure
+!stored/vote(Traveller, Vote)
     : >>(fill(F, C), F < C-1)
     <-
         generic/print("received vote");
         store/vote(Traveller, Vote);
         NewF = F+1;
         NewC = C-1;
         generic/print( "New fill", NewF, "capacity-1", NewC );
         -fill(F,C);
         +fill(NewF, C)
     : >>(fill(F, C), F == C-1)
     <-
         generic/print("received vote, start election");
         set/group/submitted();
         store/vote(Traveller, Vote);
         NewF = F+1;
         generic/print( "New fill", NewF );
         // close/group();
         !started/voting(NewF)
     .

+!clean/up/vote
    <-
	    // broker needs to remove the voters who didn't vote
	    // when you are done with clean-up, broker adds goal !start/voting in chair
	    generic/print( "clean up votes" )
	    //    clean/up/vote()
	.

// clean/group indicates whether broker has checked if all agents in group have voted.
+!started/voting(F)
    : >>(started(S), S == 0) && >>clean/group(C) && >>iteration(I)
    <-
        -started(S);
        -clean/group(C);
        +dissatisfaction( 0, F);
        // compute result of election according to given voting rule
        // add belief result/computed when done
        generic/print( "compute result", "fill is", F );
        compute/result(I)
    .

// store received diss value in Java datastructure
+!stored/diss(Traveller, Diss, Iteration)
    : >>(dissatisfaction(D, F), D < F-1)
    <-
        generic/print( "store diss", "fill", F);
        store/diss(Traveller, Diss);
        NewD = D+1;
        -dissatisfaction(D, F);
        +dissatisfaction(NewD, F)
    : >>(dissatisfaction(D, F), D == F-1)
    <-
        store/diss(Traveller, Diss);
        !done
    .

// as soon as result is computed, chair sends it to the voters and waits for diss values

+!timedout/dissvals
    : >>(wait/time/diss(X, Y), X < Y) && >>(diss(D, F), D < F)
    <-
        X = X+1;
        !timedout/dissvals
    :  >>(wait/time/diss(X,Y), X == Y) || >>(diss(D, F), D==F)
    <-
        !done
    .

+!done
    <-
        generic/print( "write h5" )
        // write/h5()
    .


