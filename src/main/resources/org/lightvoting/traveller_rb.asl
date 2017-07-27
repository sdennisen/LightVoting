voted(0).

!start.

+!start <-
generic/print("Test Traveller" );
!nextcycle.

+!nextcycle
<-
!submit/vote;
!nextcycle.

+!submit/vote
: >>my/group(Group) && >>(voted(N), N==0) && >>my/chair(Chair)
<-
  generic/print( "submit vote ");
  -voted(0);
  submit/vote(Chair).

+!submit/diss(Chair, Result)
    <-
        generic/print("submit diss for result", Result)
        // submit/diss(Chair)
    .

+!leave/group()
: >>leave/group(Broker)
<-
+group(0).

+!stay()
: >>stay(Broker)
<-
generic/print("I'm staying in the group").
