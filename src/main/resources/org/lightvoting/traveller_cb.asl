voted(0).
state(0, undefined).

!start.

+!start
    : >>state(0, S)
        <-
        -mychair(_);
        -mygroup(_);
        -state(0, S);
        +state(1, start);
        generic/print("start" );
        !group/joined;
        !nextcycle
    .

+!nextcycle
    <-
         update/cycle();
         !nextcycle
    .

// Refactor

+!group/joined
    : >>mygroup(Group) && >>state(1, S)
    <-  -state(1, S);
        +state(2, group/joined);
        generic/print( "group/joined");
        !vote/submitted
    <-
        !group/joined
//    : >>state(1, _)
//    <-
//        generic/print( "going back to start" );
//        !start
    .

+!vote/submitted
    : >>(voted(N), N==0) && >>mychair(Chair) && >>state(2, S)
    <-
        generic/print( "submit vote ");
        -voted(0);
        submit/vote(Chair);
        -state(2, S);
        +state(3, vote/submitted);
        generic/print( "vote/submitted");
        //!diss/submitted
        !received/result
    : >>state(2, _)
    <-
        !vote/submitted
    .


   +!received/result
        : >>result(Chair, Result, Intermediate, Group)
        <-
            generic/print( MyName, "add goal !diss/submitted" );
            !diss/submitted(Chair, Result, Intermediate, Group);
            +received(Intermediate, Group);
            -result(Chair, Result, Intermediate, Group)
        <-
            !received/result;
            generic/print( MyName, " added goal !received/result")
        .

// TODO re-insert?
//+!received/result
//    : >>result(Chair, Result) && >>state(3, S)
//    <-
//        -state(3, S);
//        +state(4, received/result);
//        !diss/computed
//    <-
//        !received/result
//    .

//+!diss/computed(Chair,Result)
//    <-
//        generic/print(MyName, "compute diss for result", Result);
//        compute/diss(Chair,Result)
//    .

+!diss/submitted(Chair,Result,Intermediate, Group)
    <-
        generic/print(MyName, "submit diss for result", Result, "intermediate", Intermediate, " group ", Group);
        submit/diss(Chair,Result,Intermediate,Group)
    .


// TODO re-insert?
//+!diss/computed
//    : >>result(Chair, Result) && >>state(4, S)
//    <-
//        generic/print(MyName, "compute diss for result", Result);
//        compute/diss(Chair,Result);
//        -state(4, S);
//        +state(3, vote/submitted);
//        generic/print(MyName, "diss/computed")
//    : >>state(3, _)
//    <-
//        !diss/computed
//    .



// TODO refine the following
// +state(0, undefined)
// -my/group(Group)
// !start

// not used at the moment

+!leftgroup
    : >>mygroup(G) && >>mychair(C)
    //: >>leavegroup(Broker)
    <-
        generic/print("I needed to leave my group, need a new one");
        -mygroup(G);
        -mychair(C);
        +state(0, undefined);
        !start
    .

+!done()
    : >>stay(Broker)
    <-
        generic/print("I'm staying in the group")
    .
