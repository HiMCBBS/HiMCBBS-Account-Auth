package com.himcbbs.play.serverclient.himcbbsauth.storage;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;

/**
 * An interface to store mappings from player {@link UUID} to user_id.
 * @author Terry_MC
 */
public interface Storage {
    /**
     * Get the user_id by player {@link UUID}.
     * @param uuid the player's {@link UUID}.
     * @return the user_id of the player.
     * @throws Exception if there is something wrong while getting user_id.
     * @implNote If the {@link UUID} does not exist, return {@code null}.
     */
    @Nullable
    String getUserId(UUID uuid) throws Exception;

    /**
     * Set the user_id by player {@link UUID}.
     * @param uuid the player's {@link UUID}.
     * @param userId the user_id of the player. Can be {@code null}.
     * @throws Exception if there is something wrong while setting user_id.
     * @implNote If userId is null, remove this mapping from UUID to userId.
     */
    void setUserId(UUID uuid, @Nullable String userId) throws Exception;

    /**
     * Initialize the storage.
     * @throws Exception if there is something wrong while initialing.
     */
    void init() throws Exception;

    /**
     * Get the id of your implementation.
     * @return the id of your implementation.
     */
    String id();
}
