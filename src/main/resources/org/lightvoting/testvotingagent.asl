lookForGroup(true).

// initial-goal
!main.

// initial plan (triggered by the initial goal)
+!main <-

    L= collection/list/create();
    +groupIdList(L);

    generic/print("Hello World!");
    >>chair(Chair);
    generic/print("MyChair:", Chair);

    X = MyName;
    Z = "agent 0";

    Y = X == Z ? env/open/new/group(Chair) : 0;

    generic/print("Testing Voting Agent");
    !!test;


    !nextcycle
    .

+!nextcycle <-
    >>chair(Chair);
    generic/print("MyChair:", Chair);
    !lookForGroup
    .


  // TODO: join random group
+!lookForGroup: >>lookForGroup(T) <-
     Z = true;
     T = T == Z ? env/join/group(0) : 0;
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

+!joined/group(Traveller, GroupID) <-
       generic/print("traveller ", Traveller, " joined group ", GroupID);
       -lookForGroup(true)
       .


+!message/receive(Message, AgentName) <-
     generic/print(MyName, "received", Message, AgentName,  " in cycle ", Cycle)
     .


+!new/group/opened(Traveller, Chair, GroupID): >>groupIdList(L) <-
      generic/print(MyName, ": group id: ", GroupID, " traveller: " , Traveller, " chair: " , Chair);
      L = collection/list/union(L, GroupID);
      generic/print("ID List: ", L)
      .
