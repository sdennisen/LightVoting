!main.

+!main <- generic/print("....Chair....").

+!myGroup( Traveller, GroupID) <-

    generic/print("I'm chair of group ", GroupID, " with traveller ", Traveller).

+!my/group/new/agent( Traveller, GroupID) <-

    generic/print("I'm chair of group ", GroupID, " and traveller ", Traveller, " joins").

+!start/criterion/fulfilled() <-

    generic/print("I'm now starting the election.")
   //  start/election()
.

+!vote/received(Traveller, Vote) <-
    generic/print("Receiving vote from traveller ", Traveller);
    store/vote(Traveller, Vote)
    .

+!all/votes/received() <-
    generic/print("!!!!!!!!!!!!!!!!!!!!!!! Received all votes.");
    compute/result()
    .
