package family.settings;

import dev.isxander.controlify.Controlify;
import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.event.ControlifyEvents;
import net.minecraft.class_2960;
import java.util.List;

/** Set up new profiles only; Controlify owns camera response and calibration. */
public final class KnowlesCraftController implements ControlifyEntrypoint {
 public void onControlifyInit(ControlifyApi api){
  var bindApi=dev.isxander.controlify.api.bind.ControlifyBindApi.get();
  // Some ported mods register outside Fabric's key registry. Correlate those too.
  for(var key:net.minecraft.class_310.method_1551().field_1690.field_1839){
   String name=key.method_1431();
   if(!(name.startsWith("key.journeymap.")||name.startsWith("key.travelersbackpack.")||name.startsWith("key.plasmovoice.")||name.startsWith("create.")||name.startsWith("zoomify.")))continue;
   if(!bindApi.getKeyCorrelation(key).isEmpty())continue;
   bindApi.registerBinding(builder->builder.id("fabric-key-binding-api-v1",name.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9/._-]","_"))
    .name(net.minecraft.class_2561.method_43471(name))
    .category(net.minecraft.class_2561.method_43470("KnowlesCraft"))
    .keyEmulation(key).allowedContexts(dev.isxander.controlify.bindings.BindContext.IN_GAME)
    .radialCandidate(dev.isxander.controlify.bindings.RadialIcons.FABRIC_ICON));
  }
  ControlifyEvents.CONTROLLER_CONNECTED.register(event->{
   if(!event.newController())return;
   event.controller().input().ifPresent(input->{
    event.controller().genericConfig().config().showIngameGuide=false;
    configureNewProfile(input,true);
    Controlify.instance().config().setDirty();
   });
  });
 }
 public static void configureNewProfile(dev.isxander.controlify.controller.input.InputComponent input,boolean isNew){
    if(!isNew)return;
    input.confObj().mixedInput=true;
    var actions=List.of("fabric-key-binding-api-v1:key.journeymap.map_toggle_alt",
     "fabric-key-binding-api-v1:key.travelersbackpack.inventory",
     "fabric-key-binding-api-v1:key.plasmovoice.settings",
     "fabric-key-binding-api-v1:key.journeymap.create_waypoint",
     "controlify:open_chat","controlify:change_perspective",
     "controlify:show_player_list","controlify:pick_block");
    // Missing optional mod actions never leave invalid entries in the radial menu.
    input.confObj().radialActions=actions.stream().map(class_2960::new)
     .map(id->input.getBinding(id)!=null?id:new class_2960("controlify:change_perspective")).toArray(class_2960[]::new);
 }
 public void onControllersDiscovered(ControlifyApi api){}
}
