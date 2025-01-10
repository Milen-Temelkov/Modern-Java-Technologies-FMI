package bg.sofia.uni.fmi.mjt.socialnetwork.profile;

import java.util.Comparator;

public class NumberOfFriendsComparator<U> implements Comparator<UserProfile> {
    public int compare(final UserProfile o1, final UserProfile o2) {
        if (o1.getFriends().size() < o2.getFriends().size()) {
            return 1;
        } else if (o1.getFriends().size() > o2.getFriends().size()) {
            return -1;
        } else {
            return 0;
        }
    }
}
