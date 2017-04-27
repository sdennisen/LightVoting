iteration(0).

!main.

+!main <-
    generic/print("....Chair....");
    !nextcycle
  .

+!nextcycle <-
    perceive/group();
    check/conditions();
    !nextcycle
    .

+!start/criterion/fulfilled() <-
    generic/print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!! I'm now starting the election.");

    start/election()
    .

+!vote/received(Traveller, Vote) <-
   generic/print("Receiving vote from traveller ", Traveller );
   store/vote(Vote)
   .

+!diss/received(Diss, Iteration) <-
   generic/print("Receiving dissatisfaction" );
   store/diss(Diss, Iteration)
   .


+!all/votes/received() <-
  generic/print(" All voters submitted their votes" );
  compute/result()
 .

+!all/dissValues/received(Iteration) <-
    generic/print("Received all diss values");
    remove/voter()
    .

 // remove/voter(Iteration)
//  .

// XXXXX Old Code XXXXXXX

//+group(Group) <-
//    generic/print( "XXXXXXXXXXXXXXXXXX Cycle ", Cycle, " Print Group ", Group )
//    .


//+group( Group, Agents, Open, Result) <-
//    generic/print( " XXXXXXXXXXXXXXXXXXX Print Group ", Group, " ", Agents, " ", Open, " ", Result)
//    .

//+!myGroup( Traveller, GroupID) <-

    // generic/print("I'm chair of group ", GroupID, " with traveller ", Traveller).

// +!my/group/new/agent( Traveller, GroupID) <-

  //  generic/print("I'm chair of group ", GroupID, " and traveller ", Traveller, " joins").

// +!join/group(Traveller) <-
 //    generic/print("Traveller ", Traveller, " joins my group").

//+!vote/received(Traveller, Vote) <-
//    generic/print("Receiving vote from traveller ", Traveller);
//    store/vote(Traveller, Vote)
//    .

//+!diss/received(Traveller, Diss, Iteration) <-
//    generic/print("Receiving dissatisfaction from traveller ", Traveller);
//    store/diss(Traveller, Diss, Iteration)
//    .

//+!all/dissatisfaction/received(Iteration) <-
//    generic/print("!!!!!!!!!!!!!!!!!!!!!!! Received all dissatisfaction values for iteration ", Iteration);
//    recompute/result(Iteration)
//   .

//+!election/result(Chair, Result): >>iteration(I) <-
//    generic/print("My current Result is  ", Result, "in Iteration ", I, " I'm ", Chair);
//    -iteration(I);
//    NewI = I+1;
//    +iteration(NewI);
//    generic/print("Start Iteration: ", NewI);
//    recompute/result(NewI)
//    .


//+diss/received(Traveller, Diss, Iteration) <-
 //  generic/print("Receiving dissatisfaction" );
 //  store/diss(Traveller, Diss, Iteration)
 //  .
