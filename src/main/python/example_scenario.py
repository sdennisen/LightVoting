import yaml
import sys
import numpy
import pprint


def pref2vote(p_preference):
    return numpy.round(p_preference).astype(int)


def hamming_distance(p_v1, p_v2):
    return numpy.logical_xor(p_v1, p_v2).astype(int).sum()


def committee(p_candidates, p_committee_size):
    ret = numpy.zeros_like(p_candidates)
    for i_maxv in xrange(p_candidates.max(), 0, -1):
        if (p_candidates == i_maxv).any():
            for i_idx, i_v in enumerate((p_candidates == i_maxv).astype(bool)):
                # return result if committee is full
                if ret.sum() == p_committee_size:
                    return ret
                if i_v:
                    ret[i_idx] = 1

    return ret


def tie_break(v1, v2):
    """Tie break rules for (CommitteNr, Distance) tuple (prefer lower number on equal distances)"""
    if v1[1] < v2[1]:
        return -1
    if v1[1] == v2[1] and v1[0] < v2[0]:
        return -1
    if v1[1] == v2[1] and v1[0] == v2[0]:
        return 0
    if v1[1] == v2[1] and v1[0] > v2[0]:
        return 1
    if v1[1] > v2[1]:
        return 1


def recalculate_committee(p_group, p_preferences, p_committee_size):
    """recalculate new committee for group the traveller joined"""

    l_score = numpy.zeros_like(p_group[1].get("Kv"))
    for i_groupie in p_group[1].get("traveller"):
        numpy.add(l_score, pref2vote(p_preferences[i_groupie]), l_score)

    print "new group {}: {}".format(p_group[0]+1, [i_t+1 for i_t in p_group[1].get("traveller")])
    print "\t    score: {}".format(l_score)
    p_group[1]["Kv"] = committee(l_score, p_committee_size)
    print "\t   new Kv: {}".format(p_group[1].get("Kv"))


def main():
    l_preferences = [
        numpy.array(i_pref) for i_pref in yaml.safe_load(open(sys.argv[1])).get("preferences")
        ]

    l_groups = {}
    for i_travellergroup in xrange(0, len(l_preferences), 3):

        if len(l_groups) == 0:
            print "inital groups"
            for i in xrange(3):
                l_groups[i] = {
                    "Kv": committee(
                        numpy.round(l_preferences[i]).astype(int),
                        3
                    ),
                    "traveller": set([i])
                }
                print "G{} = {} -> {}".format(
                    i+1, l_groups[i]["Kv"],
                    [i_t + 1 for i_t in l_groups[i]["traveller"]]
                )
        else:
            for i_traveller in xrange(i_travellergroup, i_travellergroup+3):

                # print group stats to console
                print "distance of traveller {}".format(i_traveller+1)
                for i_group in l_groups:
                    print "\t to group {}: {} \n\t\tTv: {}\n\t\tKv: {}".format(
                        i_group+1,
                        hamming_distance(
                            pref2vote(l_preferences[i_traveller]), l_groups[i_group].get("Kv")
                        ),
                        pref2vote(l_preferences[i_traveller]), l_groups[i_group].get("Kv")
                    )

                # add traveller to closest group
                l_groups.get(
                    # find closest group
                    sorted(
                        [
                            (
                                i_group,
                                hamming_distance(
                                    pref2vote(
                                        l_preferences[i_traveller]
                                    ),
                                    l_groups[i_group].get("Kv")
                                )
                            ) for i_group in l_groups
                            ],
                        cmp=tie_break
                    )[0][0]).get("traveller").add(i_traveller)

            print "added travellers {}, {}, {}:".format(
                i_travellergroup+1,
                i_travellergroup+2,
                i_travellergroup+3
            )
            print "recalculating all committees"
            print "groups after adding travellers:"
            for i_group in l_groups.iteritems():
                recalculate_committee(i_group, l_preferences, 3)

    pp = pprint.PrettyPrinter(indent=1)
    pp.pprint(l_groups)


if __name__ == "__main__":
    main()
