// number of created voters

created(0).
agnum(10).

!start.

+!start <-
generic/print("Test Broker" );
!agent/created;
!nextcycle.

+!nextcycle
<-
// !create/ags;
!assign/group;
!update/groups;
!nextcycle.

+!agent/created:
>>created(C) && >>agnum(N) && C < N
<-  generic/print( "C:", C );
    NewAg = create/ag(C);
    +newag(NewAg);
    NewC = C+1;
    -created(C);
    +created(NewC)
<-  !agent/created.


+!assign/group:
>>newag(Ag)
<-  -newag(Ag);
    assign/group(Ag).


+!update/groups
<- update/groups().
