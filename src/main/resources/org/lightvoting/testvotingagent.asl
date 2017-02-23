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

    Y = X == Z ? open/new/group(Chair) : 0;
    !nextcycle

    .

+!nextcycle <-
    >>chair(Chair);
    generic/print("MyChair:", Chair)
    .

+!test <-
        generic/print("Testing", MyName, "actions in cycle", Cycle);

        voting/rules/minmaxapproval/committee-from("foo", "bar", "baz");
        voting/group/initiate("pois");
        voting/group/join("group");
        voting/group/leave("group");
        voting/group/find-preferred();
        voting/send/chair/dissatisfaction(0.1);
        voting/send/chair/vote(0);

        // send a message to myself
        // message/send(MyName, "foo")

        R = generic/string/random( 12, "abcdefghijklmnopqrstuvwxyz");
        message/send("agent 0", R)
        .

+!new/group/opened(Traveller, Chair, GroupID) <-
     generic/print(MyName, ": group id: ", GroupID, " traveller: " , Traveller, " chair: " , Chair).


+!message/receive(Message, AgentName) <-
        generic/print(MyName, "received", Message, AgentName,  " in cycle ", Cycle)
        .
