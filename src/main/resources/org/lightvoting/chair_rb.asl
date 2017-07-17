// group capacity, instead of 3 it can be any other value defined by config file
capacity(3).
// initial fill level
fill(0).
// initial number of submitted diss vals
diss(0).

wait/time/vote(0).
// instead of 10 it can be any other (random) value
max/time/vote(10).

+wait/time/diss(0).
+max/time/diss(10).

!start.

// as soon as group is opened, wait for votes
+!start
<-
!wait/for/vote.

// vote clean-up is started if group capacity or timeout is reached
+!wait/for/vote
: >>wait/time/vote(X) && >>max/time/vote(Y) && X < Y && >>fill(F) && >>capacity(C) && F < C
<-
X = X+1;
!wait/for/vote.
: >>( wait/time/vote(X), max/time/vote(Y), X == Y ) || >>( fill( F ), capacity( C ), F==C )
<-
   !clean/up/vote.

+!clean/up/vote
<-
	// broker needs to remove the voters who didn't vote
	// when you are done with clean-up, broker adds belief clean/up/vote/done in chair
	clean/up/vote().

// store received vote in Java datastructure
+vote/received(Traveller, Vote)
: >>( fill(F), capacity(C), F < C )
<- store/vote(Traveller, Vote);
F = F+1.

+!start/voting
: >>clean/up/vote/done
<-
// compute result of election according to given voting rule
// add belief result/computed when done
compute/result().

// store received diss value in Java datastructure
+diss/received(Traveller, Diss)
: >>( diss(D), fill(F), D < F )
<- store/diss(Traveller, Diss);
D = D+1.

+!wait/for/diss
: >>result/computed
<-
   !wait/for/vote.
: >>( wait/time/diss(X), max/time/diss(Y), X < Y ) && >>( diss(D), fill(F), D < F )
<-
X = X+1;
:  >>( wait/time/vote(X), max/time/vote(Y), X == Y ) || >>( diss( D ), fill( F ) , D==F ) )
+!clean/up/diss.

+!clean/up/diss
<- 	// broker needs to remove the voters who didn't submit their diss
    // when you are done with clean-up, broker adds belief clean/up/diss/done in chair
    clean/up/diss().

!done
: >>clean/up/diss/done
<- write/h5().
