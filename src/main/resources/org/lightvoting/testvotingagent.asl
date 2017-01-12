// initial-goal
!main.

// initial plan (triggered by the initial goal)
+!main <-
    generic/print("Testing Voting Agent");
    !!test
    .


+!test
    : >>( myname(Name), generic/type/isstring(Name) ) <-
        generic/print("Testing", Name, "actions in cycle", Cycle);

        voting/rules/minmaxapproval/committee-from("foo", "bar", "baz");
        voting/group/initiate("pois");
        voting/group/join("group");
        voting/group/leave("group");
        voting/group/find-preferred();
        voting/send/chair/dissatisfaction(0.1);
        voting/send/chair/vote(0);

        !test
        .