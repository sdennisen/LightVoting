// initial-goal
!main.

// initial plan (triggered by the initial goal)
+!main <-
    generic/print("Testing Voting Agent");
    !!test
    .


+!test
    : >>( myid(I), generic/type/isnumeric(I) ) <-
        generic/print("Testing voting agent", I, "actions in cycle", Cycle);

        voting/rules/minmaxapproval/committee-from("foo", "bar", "baz");
        voting/group/initiate("pois");
        voting/group/join("group");
        voting/group/leave("group");
        voting/group/find-preferred();
        voting/send/chair/dissatisfaction(0.1);
        voting/send/chair/vote(0);

        !test
        .