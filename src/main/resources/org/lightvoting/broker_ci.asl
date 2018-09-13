// number of created voters

created(0).
agnum(11).

!start.

+!start
    <-
        generic/print("Test Broker" );
        !agent/created;
        !assigned/ag;
        !updated/groups;
        !decrement/counters
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

+!updated/groups
    <-
        update/groups();
        generic/print("updating groups");
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
