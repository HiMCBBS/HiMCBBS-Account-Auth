package com.himcbbs.play.serverclient.himcbbsauth.event;

import com.himcbbs.play.serverclient.himcbbsauth.storage.Storage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;

public class RegisterStorageModeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Consumer<Storage> consumer;

    public RegisterStorageModeEvent(Consumer<Storage> consumer) {
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

    public void registerStorageMode(Storage storage) {
        consumer.accept(storage);
    }
}
