package bg.sofia.uni.fmi.mjt.socialnetwork.profile;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class DefaultUserProfile implements UserProfile {

    private final String username;
    private Set<Interest> interests;
    private Set<UserProfile> friends;

    public DefaultUserProfile(String username) {
        this.username = username;
        this.interests = EnumSet.noneOf(Interest.class);
        this.friends = new LinkedHashSet<>();
    }

    /**
     * Returns the username of the user.
     * Each user is guaranteed to have a unique username.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns an unmodifiable view of the user's interests.
     *
     * @return an unmodifiable view of the user's interests
     */
    public Collection<Interest> getInterests() {
        return Collections.unmodifiableSet(interests);
    }

    /**
     * Adds an interest to the user's profile.
     *
     * @param interest the interest to be added
     * @return true if the interest is newly added, false if the interest is already present
     * @throws IllegalArgumentException if the interest is null
     */
    public boolean addInterest(Interest interest) {
        if (interest == null) {
            throw new IllegalArgumentException("interest cannot be null");
        }

        if (interests.contains(interest)) {
            return false;
        }

        interests.add(interest);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultUserProfile that = (DefaultUserProfile) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    /**
     * Removes an interest from the user's profile.
     *
     * @param interest the interest to be removed
     * @return true if the interest is removed, false if the interest is not present
     * @throws IllegalArgumentException if the interest is null
     */
    public boolean removeInterest(Interest interest) {
        if (interest == null) {
            throw new IllegalArgumentException("interest cannot be null");
        }

        if (interests.contains(interest)) {
            interests.remove(interest);
            return true;
        }

        return false;
    }

    /**
     * Return unmodifiable view of the user's friends.
     *
     * @return an unmodifiable view of the user's friends
     */
    public Collection<UserProfile> getFriends() {
        return Collections.unmodifiableSet(friends);
    }

    /**
     * Adds a user to the user's friends.
     *
     * @param userProfile the user to be added as a friend
     * @return true if the user is added, false if the user is already a friend
     * @throws IllegalArgumentException if the user is trying to add themselves as a friend,
     *                                  or if the user is null
     */
    public boolean addFriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile cannot be null");
        }

        if (userProfile == this) {
            throw new IllegalArgumentException("user cannot add himself as a friend");
        }

        if (friends.contains(userProfile)) {
            return false;
        }

        friends.add(userProfile);
        userProfile.addFriend(this);
        return true;
    }

    /**
     * Removes a user from the user's friends.
     *
     * @param userProfile the user to be removed
     * @return true if the user is removed, false if the user is not a friend
     * @throws IllegalArgumentException if the user is null
     */
    public boolean unfriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile cannot be null");
        }

        if (friends.contains(userProfile)) {
            friends.remove(userProfile);
            userProfile.unfriend(this);
            return true;

        }

        return false;
    }

    /**
     * Checks if a user is already a friend.
     *
     * @param userProfile the user to be checked
     * @return true if the user is a friend, false if the user is not a friend
     * @throws IllegalArgumentException if the user is null
     */
    public boolean isFriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile cannot be null");
        }

        return friends.contains(userProfile);
    }
}
