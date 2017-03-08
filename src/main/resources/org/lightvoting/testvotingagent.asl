name("agent 0").
lookForGroup.

// initial-goal
!main.

// TODO: naive approach: later, if there is no group, create a new one, otherwise choose one at random. -> implement in Java

// initial plan (triggered by the initial goal)

+!main : >>(name(Name), MyName == Name)
    <-
            generic/print("Hello World!");
            >>chair(Chair);
            generic/print("MyChair:", Chair);

            env/open/new/group(Chair);

            !nextcycle
            .

+!main
     : >>(name(Name), MyName != Name)
       <-
           L= collection/list/create();
           +groupIdList(L);
           generic/print(MyName, "Hello World!");
           >>chair(Chair);
           // TODO print name of chair in form chair 0, chair 1, ...
          // generic/print(MyName, "MyChair:", Chair);

            generic/print(MyName, "Testing Voting Agent");

      //      env/open/new/group(Chair);

            !nextcycle
            .

+!nextcycle <-
    >>chair(Chair);
    // TODO print name of chair in form chair 0, chair 1, ...
 //   generic/print("MyChair:", Chair);
    generic/print(MyName, "Testing Voting Agent");
    !!test
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

+!new/group/opened(Traveller, Chair)         <-
    generic/print("Traveller ", Traveller," opened group with Chair ", Chair).


+!joined/group(Traveller, Chair) <-
       generic/print(MyName, "heard that traveller ", Traveller, " joined group with Chair ", Chair)
       .

+!lookforgroup <-
       generic/print(MyName, "I'm looking for a group to join");
       env/join/group()
       .

+!submit/your/vote(Chair) <-
       generic/print(MyName, " I need to submit my vote to chair ", Chair);
       env/submit/vote(Chair)
       .
