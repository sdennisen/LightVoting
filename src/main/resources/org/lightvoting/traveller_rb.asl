group(0).
voted(0).

!start.

+!start
: >>( group(N), N==0 )
<-
!search/group();
!start.

// asks broker for a group
// broker knows group protocol
// remove belief group(0) and add belief my/group when done

// TODO implement action ask/for/group
// +!search/group()
// <-
// ask/for/group().

+!submit/vote()
: >>my/group(Group) && >>(voted(N), N==0) && >>my/chair(Chair)
<-
  -voted(0);
  submit/vote(Chair).

// TODO implement action submit/diss
//+!request/diss(Chair)
//<-
//  submit/diss(Chair).

+!leave/group(Chair)
<-
  +group(0).
