package com.github.ustc_zzzz.osuwrnm;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.GameProfileCache;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * @author ustc_zzzz
 */
@NonnullByDefault
@Plugin(id = "osuwrnm", name = "OfflineServerUsernameWebRequestNetworkManager", version = "0.1.0",
        description = "A plugin that manages the network of web requests of offline server username")
public class OfflineServerUsernameWebRequestNetworkManager
{
    private final Logger logger;

    @Inject
    public OfflineServerUsernameWebRequestNetworkManager(Logger logger)
    {
        this.logger = logger;
    }

    @Listener
    public void on(GameInitializationEvent e)
    {
        Server server = Sponge.getServer();
        if (!server.getOnlineMode())
        {
            this.logger.info("Set the game profile cache to the new one instead. ");
            GameProfileManager gameProfileManager = server.getGameProfileManager();
            gameProfileManager.setCache(new Cache(gameProfileManager.getCache()));
        }
    }

    @NonnullByDefault
    private class Cache implements GameProfileCache
    {
        private final GameProfileCache parent;

        private Cache(GameProfileCache parent)
        {
            this.parent = parent;
        }

        @Override
        public boolean add(GameProfile profile, boolean overwrite, @Nullable Instant expiry)
        {
            return this.parent.add(profile, overwrite, expiry);
        }

        @Override
        public boolean remove(GameProfile profile)
        {
            return this.parent.remove(profile);
        }

        @Override
        public Collection<GameProfile> remove(Iterable<GameProfile> profiles)
        {
            return this.parent.remove(profiles);
        }

        @Override
        public void clear()
        {
            this.parent.clear();
        }

        @Override
        public Optional<GameProfile> getById(UUID uniqueId)
        {
            return this.parent.getById(uniqueId);
        }

        @Override
        public Map<UUID, Optional<GameProfile>> getByIds(Iterable<UUID> uniqueIds)
        {
            return this.parent.getByIds(uniqueIds);
        }

        @Override
        public Optional<GameProfile> getByName(String name)
        {
            return this.parent.getByName(name);
        }

        @Override
        public Map<String, Optional<GameProfile>> getByNames(Iterable<String> names)
        {
            return this.parent.getByNames(names);
        }

        @Override
        public Collection<GameProfile> getProfiles()
        {
            return this.parent.getProfiles();
        }

        @Override
        public Collection<GameProfile> match(String name)
        {
            return this.parent.match(name);
        }

        @Override
        public Optional<GameProfile> lookupById(UUID uniqueId)
        {
            // use getById from the parent instead
            return this.parent.getById(uniqueId);
        }

        @Override
        public Map<UUID, Optional<GameProfile>> lookupByIds(Iterable<UUID> uniqueIds)
        {
            Map<UUID, Optional<GameProfile>> map = new HashMap<>();
            for (UUID uuid : uniqueIds)
            {
                map.put(uuid, this.lookupById(uuid));
            }
            return ImmutableMap.copyOf(map);
        }

        @Override
        public Optional<GameProfile> getOrLookupById(UUID uniqueId)
        {
            Optional<GameProfile> profile = this.getById(uniqueId);
            if (profile.isPresent())
            {
                return profile;
            }
            return this.lookupById(uniqueId);
        }

        @Override
        public Map<UUID, Optional<GameProfile>> getOrLookupByIds(Iterable<UUID> uniqueIds)
        {
            Map<UUID, Optional<GameProfile>> map = new HashMap<>();
            for (UUID uuid : uniqueIds)
            {
                map.put(uuid, this.getOrLookupById(uuid));
            }
            return ImmutableMap.copyOf(map);
        }

        @Override
        public Optional<GameProfile> lookupByName(String name)
        {
            // copied from net.minecraft.entity.player.EntityPlayer#getOfflineUUID instead
            byte[] bytes = ("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8);
            return Optional.of(GameProfile.of(UUID.nameUUIDFromBytes(bytes), name));
        }

        @Override
        public Map<String, Optional<GameProfile>> lookupByNames(Iterable<String> names)
        {
            Map<String, Optional<GameProfile>> map = new HashMap<>();
            for (String name : names)
            {
                map.put(name, this.lookupByName(name));
            }
            return ImmutableMap.copyOf(map);
        }

        @Override
        public Optional<GameProfile> getOrLookupByName(String name)
        {
            Optional<GameProfile> profile = this.getByName(name);
            if (profile.isPresent())
            {
                return profile;
            }
            return this.lookupByName(name);
        }

        @Override
        public Map<String, Optional<GameProfile>> getOrLookupByNames(Iterable<String> names)
        {
            Map<String, Optional<GameProfile>> map = new HashMap<>();
            for (String name : names)
            {
                map.put(name, this.getOrLookupByName(name));
            }
            return ImmutableMap.copyOf(map);
        }

        @Override
        public Optional<GameProfile> fillProfile(GameProfile profile, boolean signed)
        {
            // use the profile itself instead
            return Optional.of(profile);
        }
    }
}
