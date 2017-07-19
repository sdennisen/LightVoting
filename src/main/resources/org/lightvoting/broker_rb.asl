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
!nextcycle.

+!create/ags:
>>created(C) && >>agnum(N) && C < N
<-  generic/print( "C:", C );
    create/ag(C);
    NewC = C+1;
    -created(C);
    +created(NewC).


