package com.himcbbs.play.serverclient.himcbbsauth.event;

import com.himcbbs.play.serverclient.himcbbsauth.hook.Hook;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RegisterAuthPluginHookEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Consumer<Hook> consumer;

    public RegisterAuthPluginHookEvent(Consumer<Hook> consumer) {
        this.consumer = consumer;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void registerAuthPluginHook(Hook hook) {
        consumer.accept(hook);
    }
}
