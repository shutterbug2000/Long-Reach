package main.java;

import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R2.CraftServer;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.mojang.authlib.GameProfile;

import java.lang.reflect.*;
import java.util.*;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.EntityHuman;
import net.minecraft.server.v1_8_R2.EnumDirection;
import net.minecraft.server.v1_8_R2.PacketPlayInBlockDig.EnumPlayerDigType;

public class Main extends JavaPlugin implements Listener {

	private BlockFace face;
	private Location location1;
	private Player player;
	private Object target;
	private boolean activated;
	HashMap<String, Boolean> playerBoolean = new HashMap<String, Boolean>();
	private Button button;
	private Block block;

	  private static BlockFace getFace(Block block, Player player) {
		  Vector start = player.getEyeLocation().toVector(), direction = player.getEyeLocation().getDirection();
		  for(BlockFace face : BlockFace.values()) {
		  Vector normal = new Vector(face.getModX(), face.getModY(), face.getModZ()), point = block.getLocation().toVector().add(new Vector(0.5, 0.5, 0.5)).add(normal.multiply(0.5));
		  double cosine = normal.dot(direction.multiply(-1));
		  double distance = start.subtract(point).dot(normal);
		  double hypotenuse = distance / cosine;
		  Vector intersection = start.add(direction.multiply(hypotenuse));
		  if(contains(block, intersection)) return face;
		  }
		  return null;
		  }

		  private static boolean contains(Block block, Vector point) {
		  Vector relative = point.subtract(block.getLocation().toVector().add(new Vector(0.5, 0.5, 0.5)));
		  return Math.abs(relative.getX()) <= 0.5 && Math.abs(relative.getY()) <= 0.5 && Math.abs(relative.getZ()) <= 0.5;
		  }

	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    	 
    	 Action eAction = e.getAction();
    	 
    	if (eAction == Action.RIGHT_CLICK_AIR) {
    		
    		if(playerBoolean.get(e.getPlayer().getName()) == null | playerBoolean.get(e.getPlayer().getName()) == false){
    			
    		}else if(playerBoolean.get(e.getPlayer().getName()) == true){
    		
            block = null;
            block = e.getPlayer().getTargetBlock((HashSet<Material>)null, 100);
            
            getLogger().info(block.getType().name());
            
            Material type = block.getType();

	                  		
	                  		if(block.getType() == Material.WOOD_DOOR){
	                		 Door door = (Door) type.getNewData(block.getData());
	                         if (true != door.isOpen()) {
	                             door.setOpen(true);
	                             Block above = block.getRelative(BlockFace.UP);
	                             Block below = block.getRelative(BlockFace.DOWN);
	                             if (isDoor(above.getType())) {
	                                 block.setData(door.getData(), true);
	                                 door.setTopHalf(true);
	                                 above.setData(door.getData(), true);
	                             } else if (isDoor(below.getType())) {
	                                 door.setTopHalf(false);
	                                 below.setData(door.getData(), true);
	                                 door.setTopHalf(true);
	                                 block.setData(door.getData(), true);
	                             }
	                             block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
	                         }
		}else if(block.getType() == Material.LEVER){
	                			 Lever lever = new Lever(Material.LEVER, block.getData());
	                			 getLogger().info(Boolean.toString(lever.isPowered()));
	                			 if(lever.isPowered() == false){
	                			 lever.setPowered(true);
	                			 getLogger().info(Boolean.toString(lever.isPowered()));
	                			 block.setData(lever.getData());
	                			 }else if(lever.isPowered() == true){
	                			 lever.setPowered(false);
	                			 getLogger().info(Boolean.toString(lever.isPowered()));
	                			 }
	                			
	                			 block.setData(lever.getData());
	                			 
	                	 }else if(block.getType() == Material.STONE_BUTTON || block.getType() == Material.WOOD_BUTTON){
	                		 button = new Button(block.getType(), block.getData());
                			 getLogger().info(Boolean.toString(button.isPowered()));
                			 if(button.isPowered() == false){
                				 button.setPowered(true);
                			 getLogger().info(Boolean.toString(button.isPowered()));
                			 block.setData(button.getData());
                			 BukkitRunnable r = new BukkitRunnable() {
								
								@Override
								public void run() {
									button.setPowered(false);
									 block.setData(button.getData());
									 getLogger().info("Unpowering");
								}
							};
							r.runTaskLater(this, 20);
                			 getLogger().info(Boolean.toString(button.isPowered()));
                			 }
	                	 } else {
	                		 
	                		 {
	                			face = getFace(e.getPlayer().getTargetBlock((HashSet<Material>)null, 100), e.getPlayer());
	                			//getLogger().info(e.getBlockFace().name());
	                			//e.getPlayer().getTargetBlock((HashSet<Material>)null, 100).getRelative(e.getBlockFace()).setType(e.getPlayer().getItemInHand().getType());
	                		 
	                			List<Block> targets = e.getPlayer().getLastTwoTargetBlocks((HashSet<Byte>)null, 100);
	                			Block placeBlock = targets.get(0);
	                			placeBlock.setType(e.getPlayer().getItemInHand().getType());
	                			placeBlock.setData(e.getPlayer().getItemInHand().getData().getData());
	                			if(e.getPlayer().getItemInHand().getType() == Material.REDSTONE){
	                				placeBlock.setType(Material.REDSTONE_WIRE);
	                			}
	                	 }
	                	 }
    		}
    	}else if (eAction == Action.LEFT_CLICK_AIR) {
if(playerBoolean.get(e.getPlayer().getName()) == null | playerBoolean.get(e.getPlayer().getName()) == false){
    			
    		}else if(playerBoolean.get(e.getPlayer().getName()) == true){
    		e.getPlayer().getTargetBlock((HashSet<Material>)null, 100).setType(Material.AIR);
    		}
    	}
    	}
    	
    
    	

	
	@Override
	public void onEnable() {
		getLogger().info("onEnable has been invoked!");
		getServer().getPluginManager().registerEvents(this, this);
	}
 
	@Override
	public void onDisable() {
		getLogger().info("onDisable has been invoked!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("basic")) { // If the player typed /basic then do the following...
			if(sender instanceof Player) {
	            player = (Player) sender;
//				getLogger().info(Boolean.toString(contains((player.getWorld().getBlockAt(170,68,-178)), new Vector(171,69,-177))));
				
	            player = (Player) sender;
	            if(playerBoolean.get(player.getName()) == null){
	            	playerBoolean.put(player.getName(), true);
	            	player.sendMessage("Long-Reach mode Activated!");
	            }else if(playerBoolean.get(player.getName()) == false){
	            	playerBoolean.put(player.getName(), true);
	            	player.sendMessage("Long-Reach mode Activated!");
	            }else if(playerBoolean.get(player.getName()) == true){
	            	playerBoolean.put(player.getName(), false);
	            	player.sendMessage("Long-Reach mode Deactivated!");
	            }
	            
	          //  ((CraftPlayer) player).getHandle().playerInteractManager.a(new BlockPosition(block.getX(), block.getY(), block.getZ()), EnumDirection direction);


	            
	            
			return true;
		} //If this has happened the function will return true. 
	        // If this hasn't happened the value of false will be returned.
		return false; 
	}
		return false;
	}
	
	  public static boolean isDoor(Material type) {
	        return type == Material.WOODEN_DOOR || type == Material.IRON_DOOR;
	    }
	  

	
}
