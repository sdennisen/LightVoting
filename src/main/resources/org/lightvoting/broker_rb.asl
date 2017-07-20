// number of created voters

created(0).
agnum(10).

!start.

+!start <-
generic/print("Test Broker" );
!nextcycle.

+!nextcycle
<-
!create/ags;
!assign/group;
!update/groups;
!nextcycle.

+!create/ags:
>>created(C) && >>agnum(N) && C < N
<-  generic/print( "C:", C );
    NewAg = create/ag(C);
    +newag(NewAg);
    NewC = C+1;
    -created(C);
    +created(NewC).


+!assign/group:
>>newag(Ag)
<- assign/group(Ag);
   -newag(Ag).

+!update/groups
<- update/groups().
