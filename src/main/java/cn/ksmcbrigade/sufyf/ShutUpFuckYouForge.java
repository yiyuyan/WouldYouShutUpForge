package cn.ksmcbrigade.sufyf;

import cn.ksmcbrigade.vmr.module.Config;
import cn.ksmcbrigade.vmr.module.Module;
import cn.ksmcbrigade.vmr.uitls.ModuleUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static cn.ksmcbrigade.vmr.module.Config.configDir;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ShutUpFuckYouForge.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT,modid = ShutUpFuckYouForge.MODID)
public class ShutUpFuckYouForge {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "sufyf";

    public static ArrayList<UUID> fuckLists = new ArrayList<>();

    public static File config = new File("config/sufyf-config.json");
    public static boolean LevelClear = true;

    public ShutUpFuckYouForge() throws IOException {
        MinecraftForge.EVENT_BUS.register(this);
        save(false);
        LevelClear = JsonParser.parseString(Files.readString(config.toPath())).getAsJsonObject().get("clear").getAsBoolean();

        ModuleUtils.add(new Module("hack.name.step",false,-1,new Config(new File("Step"),config()),false){
            @Override
            public void enabled(Minecraft MC) throws IOException {
                //reload
                File pathFile = new File(configDir,getConfig().file.getPath()+".json");
                getConfig().setData(JsonParser.parseString(Files.readString(pathFile.toPath())).getAsJsonObject());

                //set
                JsonElement e = getConfig().get("block");
                if(MC.player!=null) MC.player.setMaxUpStep(e==null?1f:e.getAsFloat());
            }

            @Override
            public void disabled(Minecraft MC) {
                if(MC.player!=null) MC.player.setMaxUpStep(0.5F);
            }
        });

        ModuleUtils.add(new Module("hack.name.no_back"));
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event){
        event.getDispatcher().register(Commands.literal("ignore").then(Commands.argument("players", StringArgumentType.string()).executes(context -> {
            String args = StringArgumentType.getString(context,"players");
            String[] players = args.contains(" ")?args.split(" "):new String[]{args};
            Arrays.stream(players).toList().forEach(p -> {
                Player player = getPlayer(p);
                if(player==null) {
                    context.getSource().sendSystemMessage(Component.nullToEmpty(I18n.get("commands.found")+p));
                    return;
                }
                fuckLists.add(player.getUUID());
                context.getSource().sendSystemMessage(Component.nullToEmpty(I18n.get("commands.fack")+p));
            });
            return 0;
        })));

        event.getDispatcher().register(Commands.literal("un-ignore").then(Commands.argument("players", StringArgumentType.string()).executes(context -> {
            String args = StringArgumentType.getString(context,"players");
            String[] players = args.contains(" ")?args.split(" "):new String[]{args};
            Arrays.stream(players).toList().forEach(p -> {
                Player player = getPlayer(p);
                if(player==null) {
                    context.getSource().sendSystemMessage(Component.nullToEmpty(I18n.get("commands.found")+p));
                    return;
                }
                if(!fuckLists.contains(player.getUUID())) {
                    context.getSource().sendSystemMessage(Component.nullToEmpty(I18n.get("commands.found")+p));
                    return;
                }
                fuckLists.remove(player.getUUID());
                context.getSource().sendSystemMessage(Component.nullToEmpty(I18n.get("commands.unfack")+p));
            });
            return 0;
        })));

        event.getDispatcher().register(Commands.literal("onLevelLeaveClear").then(Commands.argument("clear", BoolArgumentType.bool()).executes(context -> {
            LevelClear = BoolArgumentType.getBool(context,"clear");
            try {
                save(true);
                context.getSource().sendSystemMessage(Component.translatable("gui.done"));
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
        })));
    }

    @SubscribeEvent
    public static void onLevelLeave(LevelEvent.Unload event){
        if(LevelClear) fuckLists.clear();
    }

    public static Player getPlayer(String name){
        Minecraft MC = Minecraft.getInstance();
        if(MC.level==null) return null;
        AtomicReference<AbstractClientPlayer> player = new AtomicReference<>();
        MC.level.players().forEach(p -> {
            if(p.getName().getString().equalsIgnoreCase(name)){
                player.set(p);
                return;
            }
        });
        return player.get();
    }

    public static void save(boolean t) throws IOException {
        if(t){
            JsonObject object = new JsonObject();
            object.addProperty("clear",LevelClear);
            Files.writeString(config.toPath(),object.toString());
        }
        else if(!config.exists()){
            JsonObject object = new JsonObject();
            object.addProperty("clear",LevelClear);
            Files.writeString(config.toPath(),object.toString());
        }
    }

    public static JsonObject config(){
        JsonObject object = new JsonObject();
        object.addProperty("block",1.5F);
        return object;
    }

    public static boolean renderCancel(){
        Minecraft MC = Minecraft.getInstance();
        Screen screen = MC.screen;
        if(MC.level==null) return false;
        return screen instanceof AbstractContainerScreen<?>;
    }
}
