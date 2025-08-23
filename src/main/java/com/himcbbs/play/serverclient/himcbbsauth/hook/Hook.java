package com.himcbbs.play.serverclient.himcbbsauth.hook;

import org.bukkit.entity.Player;

/**
 * An interface to hook into auth plugins.
 * @author Terry_MC
 */
public interface Hook {
    /**
     * Force login a player.
     * @param player the player to force login.
     * @throws Exception if there is something wrong while force logging in.
     */
    void forceLogin(Player player) throws Exception;

    /**
     * Initialize your plugin hook.
     * @throws Exception if there is something wrong while initializing your plugin hook.
     * @implNote This method is called when the plugin is enabled.
     */
    void initializeHook() throws Exception;

    /**
     * Remove your plugin hook.
     * @throws Exception if there is something wrong while removing your plugin hook.
     * @implNote This method is called when the plugin is disabled.
     */
    void removeHook() throws Exception;

    /**
     * Get the activation state of your plugin hook.
     * @return whether your plugin hook is active.
     */
    boolean isActive();

    /**
     * Get the name of the plugin that you hooked into.
     * @return the name of the plugin that you hooked into.
     * @implNote The return value of this method will be used in detecting whether the plugin that you hooked into is active. Make sure you return a correct value, if not, your hook will not be initialized!
     */
    String name();
}
