package bg.sofia.uni.fmi.mjt.socialnetwork;

import bg.sofia.uni.fmi.mjt.socialnetwork.post.Post;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.SocialFeedPost;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.Interest;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.NumberOfFriendsComparator;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;
import bg.sofia.uni.fmi.mjt.socialnetwork.exception.UserRegistrationException;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SocialNetworkImpl implements SocialNetwork {

    private Set<String> takenNames;
    private Set<UserProfile> registeredUsers;
    private Set<Post> allPosts;

    public SocialNetworkImpl() {
        this.takenNames = new HashSet<String>();
        this.registeredUsers = new HashSet<UserProfile>();
        this.allPosts = new HashSet<Post>();
    }

    /**
     * Registers a user in the social network.
     *
     * @param userProfile the user profile to register
     * @throws IllegalArgumentException  if the user profile is null
     * @throws UserRegistrationException if the user profile is already registered
     */
    public void registerUser(UserProfile userProfile) throws UserRegistrationException {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile is null");
        }

        if (registeredUsers.contains(userProfile)) {
            throw new UserRegistrationException("user already registered");
        }

        registeredUsers.add(userProfile);
    }

    /**
     * Returns all the registered users in the social network.
     *
     * @return unmodifiable set of all registered users (empty one if there are none).
     */
    public Set<UserProfile> getAllUsers() {
        return Collections.unmodifiableSet(registeredUsers);
    }

    /**
     * Posts a new post in the social network.
     *
     * @param userProfile the user profile that posts the content
     * @param content     the content of the post
     * @return the created post
     * @throws UserRegistrationException if the user profile is not registered
     * @throws IllegalArgumentException  if the user profile is null
     * @throws IllegalArgumentException  if the content is null or empty
     */
    public Post post(UserProfile userProfile, String content) throws UserRegistrationException {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile is null");
        }

        if (!registeredUsers.contains(userProfile)) {
            throw new UserRegistrationException("user not registered");
        }

        if (content == null) {
            throw new IllegalArgumentException("content is null");
        }

        if (content.isEmpty()) {
            throw new IllegalArgumentException("content is empty");
        }

        Post post = new SocialFeedPost(userProfile, content);

        allPosts.add(post);

        return post;
    }

    /**
     * Returns all posts in the social network.
     *
     * @return unmodifiable collection of all posts (empty one if there are none).
     */
    public Collection<Post> getPosts() {
        return Collections.unmodifiableSet(allPosts);
    }

    /**
     * Returns a collection of unique user profiles that can see the specified post in their feed. A
     * user can view a post if both of the following conditions are met:
     * <ol>
     *     <li>The user has at least one common interest with the author of the post.</li>
     *     <li>The user has the author of the post in their network of friends.</li>
     * </ol>
     * <p>
     * Two users are considered to be in the same network of friends if they are directly connected
     * (i.e., they are friends) or if there exists a chain of friends connecting them.
     * </p>
     *
     * @param post The post for which visibility is being determined
     * @return A set of user profiles that meet the visibility criteria (empty one if there are none).
     * @throws IllegalArgumentException if the post is <code>null</code>.
     */
    public Set<UserProfile> getReachedUsers(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("post is null");
        }

        UserProfile author = post.getAuthor();
        Collection<Interest> interest = author.getInterests();
        Set<UserProfile> reachableUsers = new HashSet<UserProfile>();

        for (UserProfile user : registeredUsers) {
            if (user.equals(author)) {
                continue;
            }

            if (isInFriendsNetwork(author, user) && hasCommonInterest(author, user)) {
                reachableUsers.add(user);
            }
        }

        return reachableUsers;
    }

    private boolean hasCommonInterest(UserProfile user1, UserProfile user2) {
        Set<Interest> commonInterest = EnumSet.copyOf(user1.getInterests());
        commonInterest.retainAll(user2.getInterests());

        return !commonInterest.isEmpty();
    }

    private boolean isInFriendsNetwork(UserProfile user1, UserProfile user2) {
        Queue<UserProfile> friendsToCheck = new LinkedList<UserProfile>(user1.getFriends());
        Set<UserProfile> visitedFriends = new HashSet<>();

        while (!friendsToCheck.isEmpty()) {
            UserProfile currentFriend = friendsToCheck.poll();

            if (currentFriend.equals(user2)) {
                return true;
            }

            if (!visitedFriends.contains(currentFriend)) {
                visitedFriends.add(currentFriend);
                friendsToCheck.addAll(currentFriend.getFriends());
            }
        }

        return false;
    }

    /**
     * Returns a set of all mutual friends between the two users.
     *
     * @param userProfile1 the first user profile
     * @param userProfile2 the second user profile
     * @return a set of all mutual friends between the two users or an empty set if there are no
     * mutual friends
     * @throws UserRegistrationException if any of the user profiles is not registered
     * @throws IllegalArgumentException  if any of the user profiles is null
     */
    public Set<UserProfile> getMutualFriends(UserProfile userProfile1, UserProfile userProfile2)
        throws UserRegistrationException {
        if (userProfile1 == null || userProfile2 == null) {
            throw new IllegalArgumentException("userProfile1 or userProfile2 is null");
        }

        if (!registeredUsers.contains(userProfile1) || !registeredUsers.contains(userProfile2)) {
            throw new UserRegistrationException("userProfile1 or userProfile2 is not registered");
        }

        Set<UserProfile> mutualFriends = new HashSet<>(userProfile1.getFriends());
        mutualFriends.retainAll(userProfile2.getFriends());

        return mutualFriends;
    }

    /**
     * Returns a sorted set of all user profiles ordered by the number of friends they have in
     * descending order.
     *
     * @return a sorted set of all user profiles ordered by the number of friends they have in
     * descending order
     */
    public SortedSet<UserProfile> getAllProfilesSortedByFriendsCount() {
        SortedSet<UserProfile> orderedUsers = new TreeSet<>(new NumberOfFriendsComparator<UserProfile>());
        orderedUsers.addAll(registeredUsers);

        return orderedUsers;
    }

}


/*
[Hint] Cannot invoke "java.util.Set.size()" because the return value of "java.util.Map.get(Object)" is null
[Hint] Cannot invoke "java.util.Set.size()" because the return value of "java.util.Map.get(Object)" is null
[Hint] Cannot invoke "java.util.Set.size()" because the return value of "java.util.Map.get(Object)" is null
[Hint] Cannot invoke "java.util.Set.size()" because the return value of "java.util.Map.get(Object)" is null
[Hint] Direct friend with common interest should see the post ==> expected: <true> but was: <false>
[Hint] Direct friend with interest should see the post ==> expected: <true> but was: <false>
[Hint] Direct friend with interest should see the post ==> expected: <true> but was: <false>
[Hint] Direct friend with interest should see the post ==> expected: <true> but was: <false>
*/