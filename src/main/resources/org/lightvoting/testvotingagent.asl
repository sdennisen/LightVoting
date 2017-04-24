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

 //  !lookforgroup;

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

        voting/rules/minmaxapproval/committee-from("foo", "bar", "baz");
        voting/group/find-preferred();
        voting/send/chair/dissatisfaction(0.1);
        voting/send/chair/vote(0);

        // send my name to agent 0
        message/send("agent 0", MyName)
        .

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

// plan for iterative voting
//+!election/result(Chair, Result, Iteration) <-
//       generic/print(MyName, "heard result", Result, "Iteration", Iteration, "from Chair", Chair);
//       generic/print(MyName, " Submit Dissatisfaction");
//       env/submit/dissatisfaction(Chair, Iteration)
//       .


+!group(ID, Chair) <-
generic/print("------------------ ", MyName, " Group ", ID," opened with Chair ", Chair)
.



