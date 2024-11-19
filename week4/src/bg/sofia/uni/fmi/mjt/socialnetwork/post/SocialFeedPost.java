package bg.sofia.uni.fmi.mjt.socialnetwork.post;

import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SocialFeedPost implements Post {

    private String uniqueId;
    private UserProfile author;
    private LocalDateTime publishedOn;
    private String content;
    private Map<ReactionType, Set<UserProfile>> reactions;

    public SocialFeedPost(UserProfile author, String content) {
        this.uniqueId = UUID.randomUUID().toString();
        this.author = author;
        this.publishedOn = LocalDateTime.now();
        this.content = content;
        this.reactions = new EnumMap<>(ReactionType.class);
    }

    /**
     * Returns the unique id of the post.
     * Each post is guaranteed to have a unique id.
     *
     * @return the unique id of the post
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Returns the author of the post.
     *
     * @return the author of the post
     */
    public UserProfile getAuthor() {
        return author;
    }

    /**
     * Returns the date and time when the post was published.
     * A post is published once it is created.
     *
     * @return the date and time when the post was published
     */
    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    /**
     * Returns the content of the post.
     *
     * @return the content of the post
     */
    public String getContent() {
        return content;
    }

    /**
     * Adds a reaction to the post.
     * If the profile has already reacted to the post, the reaction is updated to the latest one.
     * An author of a post can react to their own post.
     *
     * @param userProfile  the profile that adds the reaction
     * @param reactionType the type of the reaction
     * @return true if the reaction is added, false if the reaction is updated
     * @throws IllegalArgumentException if the profile is null
     * @throws IllegalArgumentException if the reactionType is null
     */
    public boolean addReaction(UserProfile userProfile, ReactionType reactionType) {
        if (userProfile == null || reactionType == null) {
            throw new IllegalArgumentException("userProfile or reactionType is null");
        }

        boolean changedReactions = false;

        for (ReactionType existingReaction : ReactionType.values()) {
            Set<UserProfile> existingSet = reactions.get(existingReaction);

            if (existingSet != null && existingSet.contains(userProfile)) {
                existingSet.remove(userProfile);
                if (existingSet.isEmpty()) {
                    reactions.remove(existingReaction);
                }
                changedReactions = true;
                break;
            }
        }

        Set<UserProfile> usersWithReaction = reactions.get(reactionType);

        if (usersWithReaction == null) {
            usersWithReaction = new HashSet<>();
            reactions.put(reactionType, usersWithReaction);
        }
        usersWithReaction.add(userProfile);
        return !changedReactions;
    }

    /**
     * Removes a reaction from the post.
     *
     * @param userProfile the profile that removes the reaction
     * @return true if the reaction is removed, false if the reaction is not present
     * @throws IllegalArgumentException if the profile is null
     */
    public boolean removeReaction(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile is null");
        }

        for (Map.Entry<ReactionType, Set<UserProfile>> entry : reactions.entrySet()) {

            ReactionType react = entry.getKey();
            Set<UserProfile> users = entry.getValue();

            if (users.contains(userProfile)) {
                users.remove(userProfile);
                if (users.isEmpty()) {
                    reactions.remove(react);
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all reactions to the post.
     * The returned map is unmodifiable.
     *
     * @return an unmodifiable view of all reactions to the post
     */
    public Map<ReactionType, Set<UserProfile>> getAllReactions() {
        return Collections.unmodifiableMap(reactions);
    }

    /**
     * Returns the count of a specific reaction type to the post.
     *
     * @param reactionType the type of the reaction
     * @return the count of reactions of the specified type
     * @throws IllegalArgumentException if the reactionType is null
     */
    public int getReactionCount(ReactionType reactionType) {
        if (reactionType == null) {
            throw new IllegalArgumentException("reactionType is null");
        }

        return reactions.get(reactionType).size();
    }

    /**
     * Returns the total count of all reactions to the post.
     *
     * @return the total count of all reactions to the post
     */
    public int totalReactionsCount() {
        int totalReactions = 0;

        for (Set<UserProfile> reactedUsers : reactions.values()) {
            totalReactions += reactedUsers.size();
        }

        return totalReactions;
    }
}