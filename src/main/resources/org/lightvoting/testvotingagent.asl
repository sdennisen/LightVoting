// initial-goal
!main.

// initial plan (triggered by the initial goal)
+!main <-
    generic/print("Testing Voting Agent");
    !!test;

    generic/print("Hello World!");
    >>chair(Chair);
    generic/print("MyChair:", Chair);

    X = MyName;
    Z = "agent 0";

    Y = X == Z ? env/open/new/group(Chair) : 0;
    !nextcycle
    .

+!nextcycle <-
    >>chair(Chair);
    generic/print("MyChair:", Chair)
    .

+!test <-
        generic/print("Testing", MyName, "actions in cycle", Cycle);

        voting/rules/minmaxapproval/committee-from("foo", "bar", "baz");
        voting/group/find-preferred();
        voting/send/chair/dissatisfaction(0.1);
        voting/send/chair/vote(0);


        // send my name to agent 0
        message/send("agent 0", MyName)
      //  env/join/group(0)
        .

+!joined/group(Traveller, GroupID) <-
       generic/print("traveller: ", Traveller, " joined group: ", GroupID)
       .


+!message/receive(Message, AgentName) <-
     generic/print(MyName, "received", Message, AgentName,  " in cycle ", Cycle)
     .


+!new/group/opened(Traveller, Chair, GroupID) <-
      generic/print(MyName, ": group id: ", GroupID, " traveller: " , Traveller, " chair: " , Chair)
       .
