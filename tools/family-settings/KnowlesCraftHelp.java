package family.settings;
import java.nio.file.*;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.class_2561;

/** Local help: no server permission or chat broadcast required. */
public final class KnowlesCraftHelp implements ClientModInitializer {
 public void onInitializeClient(){
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(KnowlesCraftHelp::applyKeyboardDefaultsOnce);
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(KnowlesCraftHelp::applyBackpackKeyOnce);
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(KnowlesCraftHelp::applyZoomCrouchKeysOnce);
  ClientCommandRegistrationCallback.EVENT.register((dispatcher,registries)->{
   dispatcher.register(ClientCommandManager.literal("knowlescraft").executes(c->{show(c.getSource()::sendFeedback,"help");return 1;})
    .then(ClientCommandManager.literal("help").executes(c->{show(c.getSource()::sendFeedback,"help");return 1;}))
    .then(ClientCommandManager.literal("controls").executes(c->{show(c.getSource()::sendFeedback,"controls");return 1;}))
    .then(ClientCommandManager.literal("controller").executes(c->{show(c.getSource()::sendFeedback,"controller");return 1;}))
    .then(ClientCommandManager.literal("deck").executes(c->{show(c.getSource()::sendFeedback,"deck");return 1;}))
    .then(ClientCommandManager.literal("voice").executes(c->{show(c.getSource()::sendFeedback,"voice");return 1;})));
  });
 }
 static void applyBackpackKeyOnce(net.minecraft.class_310 mc){
  Path marker=FabricLoader.getInstance().getConfigDir().resolve("knowlescraft/keyboard-backpack-r-v1.applied");
  if(Files.exists(marker))return;
  try{
   Path options=FabricLoader.getInstance().getGameDir().resolve("options.txt");
   Files.createDirectories(marker.getParent());
   if(Files.exists(options))Files.copy(options,marker.getParent().resolve("options.before-backpack-r.txt"),StandardCopyOption.REPLACE_EXISTING);
   applyBackpackKey(mc.field_1690.field_1839);
   mc.field_1690.method_1640();
   Files.writeString(marker,"R: Traveler's Backpack. Conflicting R bindings cleared.\n");
  }catch(Exception e){System.err.println("[KnowlesCraft] Could not apply backpack key: "+e);}
 }
 static void applyBackpackKey(net.minecraft.class_304[] keys){
  var r=net.minecraft.class_3675.method_15981("key.keyboard.r");
  for(var key:keys){
   if(key.method_1431().equals("key.travelersbackpack.inventory"))key.method_1422(r);
   else if(key.method_1428().equals("key.keyboard.r"))key.method_1422(net.minecraft.class_3675.field_16237);
  }
  net.minecraft.class_304.method_1426();
 }
 static void applyZoomCrouchKeysOnce(net.minecraft.class_310 mc){
  Path marker=FabricLoader.getInstance().getConfigDir().resolve("knowlescraft/keyboard-zoom-crouch-v1.applied");
  if(Files.exists(marker))return;
  try{
   Path options=FabricLoader.getInstance().getGameDir().resolve("options.txt");
   Files.createDirectories(marker.getParent());
   if(Files.exists(options))Files.copy(options,marker.getParent().resolve("options.before-zoom-crouch.txt"),StandardCopyOption.REPLACE_EXISTING);
   applyZoomCrouchKeys(mc.field_1690.field_1839);
   mc.field_1690.method_1640();
   Files.writeString(marker,"Left Ctrl: zoom; hold left Shift: crouch. Conflicting bindings cleared.\n");
  }catch(Exception e){System.err.println("[KnowlesCraft] Could not apply zoom/crouch keys: "+e);}
 }
 static void applyZoomCrouchKeys(net.minecraft.class_304[] keys){
  var ctrl=net.minecraft.class_3675.method_15981("key.keyboard.left.control");
  var shift=net.minecraft.class_3675.method_15981("key.keyboard.left.shift");
  for(var key:keys){
   String id=key.method_1431();
   if(id.equals("zoomify.key.zoom"))key.method_1422(ctrl);
   else if(id.equals("key.sneak"))key.method_1422(shift);
   else if(key.method_1428().equals("key.keyboard.left.control")||key.method_1428().equals("key.keyboard.left.shift"))key.method_1422(net.minecraft.class_3675.field_16237);
  }
  net.minecraft.class_304.method_1426();
 }
 static void applyKeyboardDefaultsOnce(net.minecraft.class_310 mc){
  Path marker=FabricLoader.getInstance().getConfigDir().resolve("knowlescraft/keyboard-emote-map-v1.applied");
  if(Files.exists(marker))return;
  try{
   Path options=FabricLoader.getInstance().getGameDir().resolve("options.txt");
   Files.createDirectories(marker.getParent());
   if(Files.exists(options))Files.copy(options,marker.getParent().resolve("options.before-keyboard-emote-map.txt"),StandardCopyOption.REPLACE_EXISTING);
   applyKeyboardBindings(mc.field_1690.field_1839);
   mc.field_1690.method_1640();
   Files.writeString(marker,"B: hold emote wheel; M: map. Conflicting B and M bindings cleared.\n");
  }catch(Exception e){System.err.println("[KnowlesCraft] Could not apply keyboard defaults: "+e);}
 }
 static void applyKeyboardBindings(net.minecraft.class_304[] keys){
  var b=net.minecraft.class_3675.method_15981("key.keyboard.b");
  var m=net.minecraft.class_3675.method_15981("key.keyboard.m");
  for(var key:keys){
   String id=key.method_1431();
   if(id.equals("key.playeremotes.wheel"))key.method_1422(b);
   else if(id.equals("key.journeymap.map_toggle_alt"))key.method_1422(m);
   else if(key.method_1428().equals("key.keyboard.b")||key.method_1428().equals("key.keyboard.m"))key.method_1422(net.minecraft.class_3675.field_16237);
  }
  net.minecraft.class_304.method_1426();
 }
 static void show(java.util.function.Consumer<class_2561> output,String topic){
  for(String line:lines(FabricLoader.getInstance().getGameDir(),topic))output.accept(class_2561.method_43470(line));
 }
 static List<String> lines(Path game,String topic){
  Map<String,String> keys=new HashMap<>();
  try{for(String line:Files.readAllLines(game.resolve("options.txt"))){int colon=line.indexOf(':');if(colon>0&&line.startsWith("key_"))keys.put(line.substring(4,colon),line.substring(colon+1));}}catch(Exception ignored){}
  String voice=key(keys,"key.plasmovoice.settings","V"),map=key(keys,"key.journeymap.map_toggle_alt","J"),zoom=key(keys,"zoomify.key.zoom","C"),bag=key(keys,"key.travelersbackpack.inventory","B");
  if(topic.equals("controller"))return List.of("KnowlesCraft · Controlify", "A: jump · Y: inventory · X: offhand · B: close · Menu: pause", "D-pad: up = chat, down = drop, left = pick block, right = radial menu.", "Hold D-pad right, choose with right stick, release: map, backpack, voice, waypoint and more.", "Select: tap map, hold 0.5s for screenshot; in menus, toggle virtual mouse.", "Complete Controlify's controller calibration. Camera sensitivity and deadzones are adjustable.", "Trackpad/mouse works alongside a new gamepad profile. Steam Input should send gamepad sticks, not mouse/WASD too.", "All mod bindings are available in Controlify settings. See KnowlesCraft-Controls.txt for setup.");
  if(topic.equals("deck"))return List.of("KnowlesCraft Â· Steam Deck","Gaming Mode: choose Steam Input's Gamepad with Mouse Trackpad template for your Prism shortcut.","Optional back buttons: L4 = V (voice), R4 = B (backpack), L5 = Left Alt (Create), R5 = J (map).","Right trackpad = mouse/left click; optional left trackpad = scroll/middle click. Steam+X opens the keyboard.","Deck presets apply once: LCD 60 FPS cap, OLED 90 FPS cap; 12 chunks, fast graphics, shaders off.","You can change those settings afterward. Windows, macOS and other Linux PCs keep their graphics settings.");
  if(topic.equals("voice"))return List.of("KnowlesCraft Â· Voice chat","Voice settings: "+voice+". Select your microphone and speakers there.","Choose Push to Talk or Voice Activation in voice settings; check Controls for your talk key.","Nearby players hear you through proximity chat. Use headphones to avoid echo.","Steam Deck uses the compatible Java voice codec automatically.");
  if(topic.equals("controls"))return List.of("KnowlesCraft Â· Your controls","Voice settings: "+voice+" Â· Full map: "+map,"Zoom: "+zoom+" Â· Backpack: "+bag,"Options â†’ Controls â†’ Key Binds shows every binding and any conflicts.","Steam Deck: your Steam controller layout maps buttons to these keys.");
  return List.of("KnowlesCraft Â· Quick help","/knowlescraft controls â€” your current useful keys","/knowlescraft voice â€” microphone and proximity chat setup","/knowlescraft controller â€” gamepad controls Â· /knowlescraft deck â€” Steam Deck setup","Prism checks pack updates before Minecraft opens. Relaunch to get new updates.","Use the same KnowlesCraft server entry at home or away; relaunch after changing networks.");
 }
 static String key(Map<String,String> keys,String name,String fallback){
  String value=keys.get(name);if(value==null)return fallback+" (default)";
  if(value.equals("key.keyboard.unknown"))return "Unbound";
  if(value.startsWith("key.keyboard."))return value.substring(13).replace('.',' ').toUpperCase(Locale.ROOT);
  if(value.startsWith("key.mouse."))return "Mouse "+value.substring(10).replace('.',' ');
  return value;
 }
}
