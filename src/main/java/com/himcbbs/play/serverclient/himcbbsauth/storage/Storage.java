package com.himcbbs.play.serverclient.himcbbsauth.storage;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;

/**
 * An interface to store mappings from player {@link UUID} to user_id
 * @author Terry_MC
 */
public interface Storage {
    /**
     * Get the user_id by player {@link UUID}.
     * @return the user_id of the player
     * @throws Exception if there is something wrong while getting user_id
     */
    @Nullable
    String getUserId(UUID uuid) throws Exception;

    /**
     * Set the user_id by player {@link UUID}
     * @throws Exception if there is something wrong while setting user_id
     * @implNote if userId is null, remove this mapping from UUID to userId.
     */
    void setUserId(UUID uuid, @Nullable String userId) throws Exception;

    /**
     * Initialize the storage.
     * @throws Exception if there is something wrong while initialing
     */
    void init() throws Exception;

    /**
     * Get the id of your implementation.
     * @return the id of your implemention
     */
    String id();
}
