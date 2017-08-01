// initial fill level is 0, capacity is 3, can be any other value defined by config file
fill(0, 3).
// initial number of submitted diss vals

wait/time/vote(0, 10).
// instead of 10 it can be any other (random) value
//max/time/vote(10).

wait/time/diss(0, 10).
//max/time/diss(10).
started(0).

!start.

// as soon as group is opened, wait for votes
+!start
    <-
        generic/print("Test Chair");
       !wait/for/vote
        // !nextcycle
    .

+!wait/for/vote
    : >>(wait/time/vote(X,Y), X < Y) && >>( fill(F, C), F < C )
    <-
        NewX = X+1;
        generic/print( "don't start election:", "time" , X, "fill", F );
        -wait/time/vote(X,Y);
        +wait/time/vote(NewX,Y);
        !wait/for/vote
    : >>(wait/time/vote(X,Y), Y == X) &&  >>fill(F, C)
    <-
        generic/print( "start election:","time" , X , "fill", F );
        // !clean/up/vote
        !start/voting(F)
    .

// store received vote in Java datastructure
+!vote/received(Traveller, Vote)
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
         store/vote(Traveller, Vote);
         NewF = F+1;
         generic/print( "New fill", NewF );
         !start/voting(NewF)
     .

+!clean/up/vote
    <-
	    // broker needs to remove the voters who didn't vote
	    // when you are done with clean-up, broker adds goal !start/voting in chair
	    generic/print( "clean up votes" )
	    //    clean/up/vote()
	.

+!start/voting(F)
    : >>(started(S), S == 0)
    <-
        -started(S);
        +dissatisfaction( 0, F);
        // compute result of election according to given voting rule
        // add belief result/computed when done
        generic/print( "compute result", "fill is", F );
        compute/result()
    .

// store received diss value in Java datastructure
+!diss/received(Traveller, Diss)
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

+!wait/for/diss
    : >>(wait/time/diss(X, Y), X < Y) && >>(diss(D, F), D < F)
    <-
        X = X+1;
        !wait/for/diss
    :  >>(wait/time/diss(X,Y), X == Y) || >>(diss(D, F), D==F)
    <-
        !done
        //!clean/up/diss
    .

//+!clean/up/diss
//    <-
//        // broker needs to remove the voters who didn't submit their diss
//        // when you are done with clean-up, broker adds goal !done in chair
//        generic/print( "clean up votes" )
//        // clean/up/diss()
//    .

+!done
    <-
        generic/print( "write h5" )
        // write/h5()
    .


