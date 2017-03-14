name("agent 0").
lookForGroup.

// initial-goal
!main.

// TODO: naive approach: later, if there is no group, create a new one, otherwise choose one at random. -> implement in Java

// initial plan (triggered by the initial goal)
+!main
    <-
            generic/print("Hello World!");
            >>chair(Chair);
            generic/print("MyChair:", Chair);

            env/open/new/group(Chair);

            !nextcycle
            .

+!nextcycle <-
    >>chair(Chair);
    generic/print("MyChair:", Chair);
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
        message/send("agent 0", MyName);


        !lookforgroup
        .

//+!new/group/opened(Traveller, Chair)         <-
  //  generic/print(MyName, " heard that traveller ", Traveller," opened group with Chair ", Chair).

//+!joined/group(Traveller, Chair) <-
//       generic/print(MyName, " heard that traveller ", Traveller, " joined group with Chair ", Chair)
//      .

+!lookforgroup <-
       env/join/group().
