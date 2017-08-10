// number of created voters

created(0).
agnum(10).

!start.

+!start
    <-
        generic/print("Test Broker" );
        !agent/created;
        !assign/group;
        !update/groups
    .

+!agent/created:
    >>created(C) && >>agnum(N) && C < N
    <-
        generic/print( "C:", C );
        NewAg = create/ag(C);
        +newag(NewAg);
        NewC = C+1;
        -created(C);
        +created(NewC);
        !agent/created
    <-
        !agent/created
    .

+!assign/group:
    >>newag(Ag)
    <-
        -newag(Ag);
        assign/group(Ag);
        !assign/group
    : true
    <-
        !assign/group
    .

+!update/groups
    <-
        update/groups();
        !update/groups
    .
