// group capacity, instead of 3 it can be any other value defined by config file
capacity(3).
// initial fill level
fill(0).
// initial number of submitted diss vals
diss(0).

wait/time/vote(0).
// instead of 10 it can be any other (random) value
max/time/vote(10).

wait/time/diss(0).
max/time/diss(10).

!start.

// as soon as group is opened, wait for votes
+!start
    <-
        generic/print("Test Chair")
        // !wait/for/vote;
        // !nextcycle.
    .

//+!nextcycle
//<- !store/vote;
//   !nextcycle.

// vote clean-up is started if group capacity or timeout is reached
+!wait/for/vote
    : >>wait/time/vote(X) && >>max/time/vote(Y) && X < Y && >>fill(F) && >>capacity(C) && F < C
    <-
        X = X+1;
        !wait/for/vote
    : >>wait/time/vote(X) && >>max/time/vote(Y) && X == Y || >>fill( F ) && >>capacity( C ) && F==C
    <-
        !clean/up/vote.

// store received vote in Java datastructure
+!vote/received(Traveller, Vote)
     <-
         generic/print( "received vote" )
     .

+!clean/up/vote
    <-
	    // broker needs to remove the voters who didn't vote
	    // when you are done with clean-up, broker adds goal !start/voting in chair
	    generic/print( "clean up votes" )
	//    clean/up/vote()
	.

// ----------------------------------------

//: >>fill(F) && >>capacity(C) && F < C && >>wait/time/vote(X) & >>max/time/vote(Y) && X < Y
//<- generic/print( "received vote" );
//store/vote(Traveller, Vote);
//F = F+1.

//+!store/vote:
//	>>vote(Traveller,Vote)
//	<- generic/print( "received vote" ).

+!start/voting
    <-
        // compute result of election according to given voting rule
        // add belief result/computed when done
        generic/print( "compute result" )
        // compute/result()
    .

// store received diss value in Java datastructure
+diss/received(Traveller, Diss)
    : >>diss(D) && >>fill(F) && D < F && >>wait/time/diss(X) && >>max/time/diss(Y) && X < Y
    <-
        generic/print( "store diss" );
        // store/diss(Traveller, Diss);
        D = D+1
    .

// as soon as result is computed, chair sends it to the voters and waits for dissatisfaction values

+!process/result
    <-
        generic/print( "send result" );
        // send/result();
        !wait/for/diss
    .

+!wait/for/diss
    <-
        !wait/for/vote
    : >>wait/time/diss(X) && >>max/time/diss(Y) && X < Y  && >>diss(D) && >>fill(F) && D < F
    <-
        X = X+1
    :  >>wait/time/vote(X) && >>max/time/vote(Y) && X == Y || >>diss(D) && >>fill(F) && D==F
    <-
        !clean/up/diss
    .

+!clean/up/diss
    <-
        // broker needs to remove the voters who didn't submit their diss
        // when you are done with clean-up, broker adds goal !done in chair
        generic/print( "clean up votes" )
        // clean/up/diss()
    .

+!done
    <-
        generic/print( "write h5" )
        // write/h5()
    .
