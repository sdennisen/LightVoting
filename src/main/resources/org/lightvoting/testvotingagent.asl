name("agent 0").
lookForGroup.
iteration(0).

// initial-goal
!main.

// initial plan (triggered by the initial goal)

+!main <-

   generic/print(MyName, "Hello World!");
   >>chair(Chair);
   generic/print(MyName, "MyChair:", Chair);
   generic/print(MyName, "Testing Voting Agent");

   perceive/env();
   join/group();
   !nextcycle
   .

+!nextcycle <-
    >>chair(Chair);
    generic/print(MyName, " MyChair:", Chair);
    generic/print(MyName, "Testing Voting Agent");
    !!test
    .

+!test  <-
        generic/print("Testing", MyName, "actions in cycle", Cycle);
        // send my name to agent 0
        message/send("agent 0", MyName)
        .

+groups(Groups) <-
        generic/print("..................Received belief on groups ", Groups )
        .

+group(Chair, Open, Result, MyGroup) <-
       generic/print("..................Received belief on group with open:", Open, " result:", Result, " mygroup:", MyGroup, " ", Chair )
       .

+!group(ID, Chair) <-
    generic/print("------------------ ", MyName, " Group ", ID," opened with Chair ", Chair)
    .

+!submit/vote(Chair) <-
    generic/print(MyName, " I need to submit my vote to chair ", Chair);
    submit/vote(Chair)
    .

// plan for basic voting
+!election/result(Chair, Result) <-
 generic/print(MyName, " heard result ", Result, " from chair ", Chair)
 .

 // plan for iterative voting
+!election/result(Chair, Result, Iteration) <-
  //    generic/print(MyName, "heard result", Result, "Iteration", Iteration, "from Chair", Chair);
  //    generic/print(MyName, " Submit Dissatisfaction");
      submit/dissatisfaction(Chair, Iteration, Result)
      .


// XXXXXXXXXXXX Old code XXXXXXXXXXXX
// TODO if necessary, reinsert in test  voting/group/find-preferred();
// TODO if necessary, reinsert in test  voting/rules/minmaxapproval/committee-from("foo", "bar", "baz");
// TODO if necessary, reinsert in test  voting/send/chair/dissatisfaction(0.1);
// TODO if necessary, reinsert in test  voting/send/chair/vote(0);

 //  !lookforgroup;

 // +!lookforgroup <-
    //    generic/print(MyName, "I'm looking for a group to join");
    //    join/group()
    //    env/join/group()
    //    .

 //+!submit/your/vote(Chair) <-
 //       generic/print(MyName, " I need to submit my vote to chair ", Chair);
 //       env/submit/vote(Chair)
 //       .

 // plan for basic voting
 //+!election/result(Chair, Result) <-
 //       generic/print(MyName, " heard result ", Result, " from Chair ", Chair)
 //       .



//+!ack() <-
//      verify/ack()
//      .
