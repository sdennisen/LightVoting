// number of created voters

created(0).
agnum(20).

!start.

+!start
    <-
        generic/print("Test Broker" );
        !agent/created;
        !assigned/ag;
        !decrement/counters;
        !updated/groups
    .

+!agent/created:
    >>created(C) && >>agnum(N) && C < N
    <-
        generic/print( "C:", C );
        NewAg = create/ag(C);
        +newag(NewAg, 0);
        NewC = C+1;
        -created(C);
        +created(NewC);
        !agent/created
    <-
        !agent/created
    .

+!assigned/ag:
    >>newag(Ag, I)
    <-  generic/print(Ag, "I: ", I);
        -newag(Ag, I);
        assign/group(Ag);
        !assigned/ag
    : true
    <-
        !assigned/ag
    .

+!updated/groups
    <-
        update/groups();
        !updated/groups
    .


+!decrement/counters
    <-
        decrement/counters();
        !decrement/counters
    .

+!done
    <-
        generic/print("we are done with this run")
    .
