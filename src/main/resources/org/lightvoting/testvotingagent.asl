// initial-goal
!main.

// initial plan (triggered by the initial goal)
+!main <-
    generic/print("Testing Voting Agent");
    !!test
    .

/* TODO ensure that all messages are received */

+!test
    : >>( myname(MyName), generic/type/isstring(MyName) ) <-
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

+!message/receive(Message, AgentName)
    : >>( myname(MyName), generic/type/isstring(MyName) ) <-
        generic/print(MyName, "received", Message, AgentName,  " in cycle ", Cycle)
        .
