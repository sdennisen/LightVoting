lookForGroup(1).
name("agent 0").

// initial-goal
!main.

// initial plan (triggered by the initial goal)
+!main: >>(name(Name), MyName == Name)
    <-

            L= collection/list/create();
            +groupIdList(L);

            generic/print("Hello World!");
            >>chair(Chair);
            generic/print("MyChair:", Chair);

            env/open/new/group(Chair);

            generic/print(MyName, "Testing Voting Agent");
            !!test;


            !nextcycle
            .

+!main
    : >>(name(Name), MyName != Name)
        <-
            generic/print(MyName, "Hello World!");
            >>chair(Chair);
            generic/print(MyName, "MyChair:", Chair);

            generic/print(MyName, "Testing Voting Agent");
            !!test;


            !nextcycle
            .

+!nextcycle <-
    >>chair(Chair);
    generic/print("MyChair:", Chair)
    .

  // TODO: join random group
  // TODO: implement randomInt() in AgentSpeak(L++) instead of rounding double value

+!lookForGroup <-
      >>groupIdList(L);
      generic/print("List size: ", collection/size(L));
      //    I = math/statistic/randomsimple() * collection/size(L);
      // assuming there are 3 groups
      I = math/statistic/randomsimple() * 3;
      generic/print(MyName, " Random number: ", I);
      //   Z = true;
      //    T = T == Z ? env/join/group(0) : 0
      env/join/group(0)
     .


+!test  <-
        generic/print("Testing", MyName, "actions in cycle", Cycle);

        voting/rules/minmaxapproval/committee-from("foo", "bar", "baz");
        voting/group/find-preferred();
        voting/send/chair/dissatisfaction(0.1);
        voting/send/chair/vote(0);


        // send my name to agent 0
        message/send("agent 0", MyName);
        !lookForGroup
         .

+!joined/group(Traveller, GroupID) <-
       generic/print("traveller ", Traveller, " joined group ", GroupID)
       .


+!message/receive(Message, AgentName) <-
     generic/print(MyName, "received", Message, AgentName,  " in cycle ", Cycle)
     .

+!new/group/opened(Traveller, Chair, GroupID): >>groupIdList(L) <-
      generic/print("traveller ", Traveller, " opened group ", GroupID);
      L = collection/list/union(L, GroupID);
      generic/print("ID List: ", L)
      .

