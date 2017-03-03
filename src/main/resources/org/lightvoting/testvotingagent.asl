name("agent 0").
lookForGroup.

// initial-goal
!main.

// TODO: if there is no group, create a new one, otherwise choose one at random.

// initial plan (triggered by the initial goal)
+!main //: >>(name(Name), MyName == Name)
    <-

            L= collection/list/create();
            +groupIdList(L);
            generic/print("Hello World!");
            >>chair(Chair);
            generic/print("MyChair:", Chair);

            env/open/new/group(Chair);

            !nextcycle
            .

//+!main
//      : >>(name(Name), MyName != Name)
//        <-
//            L= collection/list/create();
//            +groupIdList(L);
//            generic/print(MyName, "Hello World!");
//            >>chair(Chair);
//            generic/print(MyName, "MyChair:", Chair);

//            generic/print(MyName, "Testing Voting Agent");

//      //      env/open/new/group(Chair);

//            !nextcycle
//            .

+!nextcycle <-
    >>chair(Chair);
    generic/print("MyChair:", Chair);
    generic/print(MyName, "Testing Voting Agent");
    !!test
    .

     // TODO current fix: join group 0 by default -> better: if you don't see a group, open a new one.

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


+!lookForGroup : >>(groupIdList(L), (collection/size(L) !=0))
<-
                generic/print("Cycle: ", Cycle, " List size: ", collection/size(L));
                generic/print("Cycle: ", Cycle, " List: ", L);

                I = math/statistic/randomsimple() * collection/size(L);

                J = math/floor(I);

                K = collection/list/get(L, J);

                env/join/group(K)
.

+!lookForGroup : >>(groupIdList(L), (collection/size(L) ==0))
<-
                 generic/print("Cycle: ", Cycle, " List size: ", collection/size(L));
                 generic/print("Cycle: ", Cycle, " List: ", L);

                 env/join/group(0)
.




+!joined/group(Traveller, GroupID) <-
       generic/print("traveller ", Traveller, " joined group ", GroupID)
       .


+!message/receive(Message, AgentName) <-
     generic/print(MyName, "received", Message, AgentName,  " in cycle ", Cycle)
     .

+!new/group/opened(Traveller, Chair, GroupID): >>groupIdList(L) <-
      generic/print(MyName, " heard that traveller ", Traveller, " opened group ", GroupID);
      NewL = collection/list/union(L, 0, GroupID);
      generic/print("ID List: ", NewL);
      -groupIdList(L);
      +groupIdList(NewL)
      .


//+!lookForGroup <-
//      >>groupIdList(L);
//      generic/print("List size: ", collection/size(L));
//      //    I = math/statistic/randomsimple() * collection/size(L);
//      // assuming there are 3 groups
//      I = math/statistic/randomsimple() * 3;
//      generic/print(MyName, " Random number: ", I);
//      //   Z = true;
//      //    T = T == Z ? env/join/group(0) : 0
//      env/join/group(0)
//     .
