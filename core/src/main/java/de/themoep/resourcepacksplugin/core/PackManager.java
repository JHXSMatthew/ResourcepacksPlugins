package de.themoep.resourcepacksplugin.core;

import de.themoep.resourcepacksplugin.core.events.IResourcePackSelectEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Phoenix616 on 25.03.2015.
 */
public class PackManager {

    private final ResourcepacksPlugin plugin;
    /**
     * packname -> ResourcePack
     */
    private Map<String, ResourcePack> packmap = new HashMap<String, ResourcePack>();

    /**
     * packhash -> packname 
     */
    private Map<String, String> hashmap = new HashMap<String, String>();
    
    /**
     * packurl -> packname 
     */
    private Map<String, String> urlmap = new HashMap<String, String>();

    /**
     * playerid -> packname 
     */
    private Map<UUID, String> usermap = new ConcurrentHashMap<UUID, String>();

    /**
     * Name of the empty pack, null if none is set
     */
    private ResourcePack empty = null;
    
    /**
     * Name of the global pack, null if none is set
     */
    private ResourcePack global = null;

    /**
     * List of the names of global secondary packs
     */
    private List<String> globalSecondary = new ArrayList<String>();
    
    /**
     * servername -> packname 
     */
    private Map<String, String> servermap = new HashMap<String, String>();

    /**
     * servername -> List of the names of secondary packs
     */
    private Map<String, List<String>> serversecondarymap = new HashMap<String, List<String>>();

    public PackManager(ResourcepacksPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registeres a new resource pack with the packmanager
     * @param pack The resourcepack to register
     * @return If a pack with that name was known before it returns the past pack, null if none was known
     */
    public ResourcePack addPack(ResourcePack pack) {
        hashmap.put(pack.getHash(), pack.getName().toLowerCase());
        urlmap.put(pack.getUrl(), pack.getName().toLowerCase());
        return packmap.put(pack.getName().toLowerCase(), pack);
    }

    /**
     * Get the resourcepack by its name
     * @param name The name of the pack to get
     * @return The resourcepack with that name, null if there is none
     */
    public ResourcePack getByName(String name) {
        return packmap.get(name.toLowerCase());
    }
    
    /**
     * Get the resourcepack by its hash
     * @param hash The hash of the pack to get
     * @return The resourcepack with that hash, null if there is none
     */
    public ResourcePack getByHash(String hash) {
        String name = hashmap.get(hash);
        return (name == null) ? null : getByName(name);
    }

    /**
     * Get the resourcepack by its url
     * @param url The url of the pack to get
     * @return The resourcepack with that url, null if there is none
     */
    public ResourcePack getByUrl(String url) {
        String name = urlmap.get(url);
        return (name == null) ? null : getByName(name);
    }

    /**
     * Set the empty Resource Pack
     * @param pack The pack to set as empty pack
     * @return The previous empty pack, null if none was set
     */
    public ResourcePack setEmptyPack(ResourcePack pack) {
        ResourcePack rp = getEmptyPack();
        empty = pack;
        return rp;
    }

    /**
     * Set the empty Resource Pack
     * @param packname The name of the pack to set as empty pack
     * @return The previous empty pack, null if none was set
     */
    public ResourcePack setEmptyPack(String packname) {
        return setEmptyPack(getByName(packname));
    }

    /**
     * Get the empty Resource Pack
     * @return The empty pack, null if none is set
     */
    public ResourcePack getEmptyPack() {
        return empty;
    }
    
    /**
     * Set the global Resource Pack
     * @param pack The pack to set as global
     * @return The previous global pack, null if none was set
     */
    public ResourcePack setGlobalPack(ResourcePack pack) {
        ResourcePack rp = getGlobalPack();
        global = pack;
        return rp;
    }

    /**
     * Set the global Resource Pack
     * @param packname The name of the pack to set as global
     * @return The previous global pack, null if none was set
     */
    public ResourcePack setGlobalPack(String packname) {
        return setGlobalPack(getByName(packname));
    }

    /**
     * Get the global Resource Pack
     * @return The global pack, null if none is set
     */
    public ResourcePack getGlobalPack() {
        return global;
    }

    /**
     * Add a secondary global Resource Pack
     * @param pack The pack to add to the list of secondary ones
     * @return False if the pack already was in the list; True if not
     */
    public boolean addGlobalSecondary(ResourcePack pack) {
        return addGlobalSecondary(pack.getName());
    }

    /**
     * Add a secondary global Resource Pack
     * @param packname The name of the pack to add to the list of secondary ones
     * @return False if the pack already was in the list; True if not
     */
    public boolean addGlobalSecondary(String packname) {
        return !isGlobalSecondary(packname) && getGlobalSecondary().add(packname.toLowerCase());
    }

    /**
     * Get if a pack is in the list of secondary global Resource Packs
     * @param pack The pack to check
     * @return True if it is a global secondary pack, false if not
     */
    public boolean isGlobalSecondary(ResourcePack pack) {
        return pack != null && isGlobalSecondary(pack.getName());
    }

    /**
     * Get if a pack is in the list of secondary global Resource Packs
     * @param packname The name of the pack to check
     * @return True if it is a global secondary pack, false if not
     */
    public boolean isGlobalSecondary(String packname) {
        return getGlobalSecondary().contains(packname.toLowerCase());
    }

    /**
     * Get the list of global seconday packs
     * @return A list of packnames that are global secondary packs
     */
    public List<String> getGlobalSecondary() {
        return globalSecondary;
    }
    
    /**
     * Get the resourcepack of a server
     * @param server The name of the server, "!global" for the global pack
     * @return The resourcepack of the server, null if there is none
     */
    public ResourcePack getServerPack(String server) {
        String name = servermap.get(server.toLowerCase());
        return (name == null) ? null : getByName(name);
    }
    
    /**
     * Get the resourcepack of a user
     * @param playerid The UUID of this player
     * @return The resourcepack the player has selected, null if he has none/isn't known
     */
    public ResourcePack getUserPack(UUID playerid) {
        String name = usermap.get(playerid);
        return (name == null) ? null : getByName(name);
    }
    
    /**
     * Set the resourcepack of a user
     * @param playerid The UUID of this player
     * @param pack The resourcepack of the user
     * @return The resourcepack the player had selected previous, null if he had none before
     */
    public ResourcePack setUserPack(UUID playerid, ResourcePack pack) {
        String previous = usermap.put(playerid, pack.getName());
        return (previous == null) ? null : getByName(previous);
    }

    /**
     * Clear the resourcepack of a user
     * @param playerid The UUID of this player
     * @return The resourcepack the player had selected previous, null if he had none before
     */
    public ResourcePack clearUserPack(UUID playerid) {
        String previous = usermap.remove(playerid);
        return (previous == null) ? null : getByName(previous);
    }
    

    /**
     * Add a server to a resourcepack
     * @param server The server this pack should be active on
     * @param pack The resourcepack
     */
    public void addServer(String server, ResourcePack pack) {
        pack.addServer(server);
        servermap.put(server.toLowerCase(), pack.getName().toLowerCase());
    }
    
    /**
     * Removes the pack of a server
     * @param server The server the pack should get removed from
     * @return True if the server had a pack, false if not
     */
    public boolean removeServer(String server) {
        String packname = servermap.remove(server.toLowerCase());
        if(packname != null && packmap.containsKey(packname)) {
            return packmap.get(packname).removeServer(server);
        }
        return false;
    }

    /**
     * Add a secondary server Resource Pack
     * @param server The server to add a secondary pack to
     * @param pack The pack to add to the list of secondary ones
     * @return False if the pack already was in the list; True if not
     */
    public boolean addServerSecondary(String server, ResourcePack pack) {
        return addServerSecondary(server, pack.getName());
    }

    /**
     * Add a secondary global Resource Pack
     * @param server The server to add a secondary pack to
     * @param packname The name of the pack to add to the list of secondary ones
     * @return False if the pack already was in the list; True if not
     */
    public boolean addServerSecondary(String server, String packname) {
        if(isServerSecondary(server, packname)) {
            return false;
        }
        List<String> serverSecondaries = getServerSecondary(server);
        serverSecondaries.add(packname.toLowerCase());
        serversecondarymap.put(server.toLowerCase(), serverSecondaries);
        return true;
    }

    /**
     * Get if a pack is in the list of secondary Resource Packs for this server
     * @param server The check the secondary pack of
     * @param pack The pack to check
     * @return True if it is a global secondary pack, false if not
     */
    public boolean isServerSecondary(String server, ResourcePack pack) {
        return pack != null && isServerSecondary(server, pack.getName());
    }

    /**
     * Get if a pack is in the list of secondary Resource Packs for this server
     * @param server The server to add a secondary pack to
     * @param packname The name of the pack to check
     * @return True if it is a global secondary pack, false if not
     */
    public boolean isServerSecondary(String server, String packname) {
        return getServerSecondary(server).contains(packname.toLowerCase());
    }

    /**
     * Get the list of secondary packs of a specific server
     * @param server The name of the server
     * @return The list of secondary packs; empty if none found
     */
    public List<String> getServerSecondary(String server) {
        return serversecondarymap.containsKey(server.toLowerCase()) ? serversecondarymap.get(server.toLowerCase()) : new ArrayList<String>();
    }

    public void applyPack(UUID playerId, String serverName) {
        ResourcePack pack = getApplicablePack(playerId, serverName);
        if(pack != null) {
            plugin.setPack(playerId, pack);
        }
    }

    /**
     * Get the pack the player should have on that server
     * @param playerId The UUID of the player
     * @param serverName The name of the server
     * @return The pack for that server; <tt>null</tt> if he should have none
     */
    public ResourcePack getApplicablePack(UUID playerId, String serverName) {
        ResourcePack prev = getUserPack(playerId);
        ResourcePack pack = null;
        IResourcePackSelectEvent.Status status = IResourcePackSelectEvent.Status.UNKNOWN;
        if(isGlobalSecondary(prev) && checkPack(playerId, prev, IResourcePackSelectEvent.Status.SUCCESS) == IResourcePackSelectEvent.Status.SUCCESS) {
            return null;
        }
        if(serverName != null && !serverName.isEmpty()) {
            if(isServerSecondary(serverName, prev) && checkPack(playerId, prev, IResourcePackSelectEvent.Status.SUCCESS) == IResourcePackSelectEvent.Status.SUCCESS) {
                return null;
            }
            ResourcePack serverPack = getServerPack(serverName);
            status = checkPack(playerId, serverPack, status);
            if(status == IResourcePackSelectEvent.Status.SUCCESS) {
                pack = serverPack;
            } else if(prev != null || serverPack != null){
                List<String> serverSecondary = getServerSecondary(serverName);
                for(String secondaryName : serverSecondary) {
                    ResourcePack secondaryPack = getByName(secondaryName);
                    status = checkPack(playerId, secondaryPack, status);
                    if(status == IResourcePackSelectEvent.Status.SUCCESS) {
                        pack = secondaryPack;
                        break;
                    }
                }
            }
        }
        if(pack == null) {
            ResourcePack globalPack = getGlobalPack();
            status = checkPack(playerId, globalPack, status);
            if(status == IResourcePackSelectEvent.Status.SUCCESS) {
                pack = globalPack;
            } else if(prev != null || globalPack != null){
                List<String> globalSecondary = getGlobalSecondary();
                for(String secondaryName : globalSecondary) {
                    ResourcePack secondaryPack = getByName(secondaryName);
                    status = checkPack(playerId, secondaryPack, status);
                    if(status == IResourcePackSelectEvent.Status.SUCCESS) {
                        pack = secondaryPack;
                        break;
                    }
                }
            }
        }

        if(pack != null) {
            status = IResourcePackSelectEvent.Status.SUCCESS;
        }

        IResourcePackSelectEvent selectEvent = plugin.callPackSelectEvent(playerId, pack, status);
        pack = selectEvent.getPack();
        if(pack == null && prev != null && !prev.equals(getEmptyPack())) {
            pack = getEmptyPack();
        }
        if(pack != null && pack.equals(prev)) {
            pack = null;
        }
        return pack;
    }

    private IResourcePackSelectEvent.Status checkPack(UUID playerId, ResourcePack pack, IResourcePackSelectEvent.Status status) {
        if(pack == null) {
            return status;
        }
        boolean rightFormat = pack.getFormat() <= plugin.getPlayerPackFormat(playerId);
        boolean hasPermission = !pack.isRestricted() || plugin.checkPermission(playerId, pack.getPermission());
        if(rightFormat && hasPermission) {
            return IResourcePackSelectEvent.Status.SUCCESS;
        }
        if(status != IResourcePackSelectEvent.Status.NO_PERM_AND_WRONG_VERSION) {
            if(!rightFormat) {
                if(!hasPermission || status == IResourcePackSelectEvent.Status.NO_PERMISSION) {
                    status = IResourcePackSelectEvent.Status.NO_PERM_AND_WRONG_VERSION;
                } else {
                    status = IResourcePackSelectEvent.Status.WRONG_VERSION;
                }
            }
            if(!hasPermission) {
                if(!rightFormat || status == IResourcePackSelectEvent.Status.WRONG_VERSION) {
                    status = IResourcePackSelectEvent.Status.NO_PERM_AND_WRONG_VERSION;
                } else {
                    status = IResourcePackSelectEvent.Status.NO_PERMISSION;
                }
            }
        }
        return status;
    }

    /**
     * Get a list of all packs
     * @return A new array list of packs
     */
    public List<ResourcePack> getPacks() {
        return new ArrayList<ResourcePack>(packmap.values());
    }
}
