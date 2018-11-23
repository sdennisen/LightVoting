// number of created voters

created(0).
agnum(41).

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
    >>newag(Ag, I) && >>allgroupsready(S)
    <-  generic/print(Ag, "I: ", I);
        -newag(Ag, I);
        -allgroupsready(S);
        assign/group(Ag);
        !assigned/ag
    <-
        generic/print( "not assigning ag" );
        !assigned/ag
    .

+!decrement/counters
    <-
        decrement/counters();
        !decrement/counters
    .

+!updated/groups
    <-
        update/groups();
        !updated/groups
    .

+!done
    <-
        generic/print("we are done with this run")
    .
