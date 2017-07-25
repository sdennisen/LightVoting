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
<- generic/print("Test Chair").
