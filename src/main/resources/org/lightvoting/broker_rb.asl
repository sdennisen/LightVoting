// number of created voters

created(0).
agnum(10).

!start.

+!start
    <-
        generic/print("Test Broker" );
        !agent/created;
        !assigned/ag;
        !update/groups
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

+!update/groups
    <-
        update/groups();
        !update/groups
    .
