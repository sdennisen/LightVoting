// initial-goal
!main.

// initial plan (triggered by the initial goal)
+!main <-
    generic/print("Hello World!");
    ID = my/id();
    generic/print("My Id is", ID);
    !mygoal
    .

+!mygoal <-
    generic/print("Current Cycle:", Cycle);
    my/new-action();
    !mygoal
    .