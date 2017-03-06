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
        message/send("agent 0", MyName)

        .

+!joined/group(Traveller, GroupID) <-
       generic/print("traveller ", Traveller, " joined group ", GroupID)
       .

+groupIdList(L): (collection/size(L) != 0) <-
generic/print(MyName, " List greater than 0 ", L );
S = collection/size(L);
generic/print("List size: ", S);
I = math/statistic/randomsimple() * collection/size(L);

J = math/floor(I);

generic/print("J :", J);

K = collection/list/get(L, J);

generic/print("K: ",  K);

env/join/group(0)

//env/join/group(K)

//env/join/group(0)
.

+!new/group/opened(Traveller, Chair, GroupID)  <-
     generic/print("Traveller ", Traveller, " opens Group ", GroupID);
    !insertNewId(Traveller, GroupID)
    .

// TODO remove isempty condition

+!insertNewId(Traveller, GroupID): >>(groupIdList(L), (collection/list/isempty(L))) <-
    generic/print("GroupID: ", GroupID);
    NewL = collection/list/create(GroupID);
    -groupIdList(L);
    +groupIdList(NewL)
    .



+groupIdList(L) <-

 generic/print(MyName, " ID List: ", L).


//+!message/receive(Message, AgentName) <-
//     generic/print(MyName, "received", Message, AgentName,  " in cycle ", Cycle)
//     .


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
