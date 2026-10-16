package family.settings;

import dev.isxander.controlify.Controlify;
import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.event.ControlifyEvents;
import net.minecraft.class_2960;
import java.util.List;

/** Set up new profiles only; Controlify owns camera response and calibration. */
public final class KnowlesCraftController implements ControlifyEntrypoint {
 private dev.isxander.controlify.api.bind.InputBindingSupplier mapScreenshot,touchpadSelect;
 private SelectFlow selectFlow=new SelectFlow();
 private static final java.util.Set<dev.isxander.controlify.controller.ControllerEntity> PAUSE_BLOCKED=java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());
 private static final java.util.Map<net.minecraft.class_433,PauseGesture> PAUSE_GESTURES=new java.util.WeakHashMap<>();
 private dev.isxander.controlify.controller.ControllerEntity gestureController;
 public void onControlifyInit(ControlifyApi api){
  KnowlesCraftLayout.install();
  net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT.register((mc,screen,width,height)->{
   if(screen instanceof net.minecraft.class_433 pause)PAUSE_GESTURES.put(pause,new PauseGesture(Controlify.instance().getCurrentController().filter(c->c.input().isPresent()).map(KnowlesCraftController::pauseButtonDown).orElse(false)));
  });
  dev.isxander.controlify.screenop.ScreenProcessorProvider.registerProvider(net.minecraft.class_429.class,OptionsBackProcessor::new);
  dev.isxander.controlify.screenop.ScreenProcessorProvider.registerProvider(net.minecraft.class_5522.class,ParentBackProcessor::new);
  dev.isxander.controlify.screenop.ScreenProcessorProvider.registerProvider(net.minecraft.class_447.class,ParentBackProcessor::new);
  dev.isxander.controlify.screenop.ScreenProcessorProvider.registerProvider(net.minecraft.class_457.class,ParentBackProcessor::new);
  dev.isxander.controlify.screenop.ScreenProcessorProvider.registerProvider(net.minecraft.class_407.class,CancelBackProcessor::new);
  dev.isxander.controlify.screenop.ScreenProcessorProvider.registerProvider(journeymap.client.ui.fullscreen.Fullscreen.class,
   MapBackProcessor::new);
  var bindApi=dev.isxander.controlify.api.bind.ControlifyBindApi.get();
  mapScreenshot=bindApi.registerBinding(builder->builder.id("knowlescraft","map_screenshot")
   .name(net.minecraft.class_2561.method_43470("Map / Hold for Screenshot"))
   .description(net.minecraft.class_2561.method_43470("Tap to open the map. Hold 0.5 seconds for one screenshot."))
   .category(net.minecraft.class_2561.method_43470("KnowlesCraft"))
   .defaultInput(new dev.isxander.controlify.bindings.input.ButtonInput(new class_2960("controlify:button/back")))
   .allowedContexts(dev.isxander.controlify.bindings.BindContext.IN_GAME));
  touchpadSelect=bindApi.registerBinding(builder->builder.id("knowlescraft","touchpad_select")
   .name(net.minecraft.class_2561.method_43470("Touchpad: Map / Hold for Screenshot"))
   .category(net.minecraft.class_2561.method_43470("KnowlesCraft"))
   .defaultInput(new dev.isxander.controlify.bindings.input.ButtonInput(dev.isxander.controlify.controller.input.GamepadInputs.TOUCHPAD_1_BUTTON))
   .allowedContexts(dev.isxander.controlify.bindings.BindContext.IN_GAME));
  ControlifyEvents.CONTROLLER_STATE_UPDATE.register(event->filterPauseInput(event.controller()));
  ControlifyEvents.ACTIVE_CONTROLLER_TICKED.register(event->{
   var controller=event.controller();var mc=net.minecraft.class_310.method_1551();
   handlePauseScreen(mc,pauseButtonDown(controller));
   if(gestureController!=controller){selectFlow=new SelectFlow();gestureController=controller;}
   var state=controller.input().orElseThrow().rawStateNow();
   boolean physical=state.isButtonDown(dev.isxander.controlify.controller.input.GamepadInputs.BACK_BUTTON)||state.isButtonDown(dev.isxander.controlify.controller.input.GamepadInputs.TOUCHPAD_1_BUTTON);
   boolean bound=mapScreenshot.on(controller).digitalNow()||touchpadSelect.on(controller).digitalNow();
   int action=selectFlow.update(physical,bound,mc.field_1755 instanceof journeymap.client.ui.fullscreen.Fullscreen,mc.field_1755==null&&mc.field_1724!=null,System.nanoTime());
   if(action==1)journeymap.client.ui.UIManager.INSTANCE.openFullscreenMap();
   if(action==2)dev.isxander.controlify.bindings.ControlifyBindings.TAKE_SCREENSHOT.on(controller).fakePress();
   if(action==3)journeymap.client.ui.UIManager.INSTANCE.closeAll();
   if(action==4){var mouse=Controlify.instance().virtualMouseHandler();if(!mouse.isVirtualMouseEnabled())mouse.enableVirtualMouse();}

  });
  ControlifyEvents.CONTROLLER_DISCONNECTED.register(event->{selectFlow=new SelectFlow();gestureController=null;PAUSE_GESTURES.clear();PAUSE_BLOCKED.clear();});
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
    configureSelectDefaults(input);
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
 public static boolean handleMapSelect(dev.isxander.controlify.controller.input.ControllerStateView state,Runnable closeMap,Runnable keepCursor){
  if(state.isButtonDown(dev.isxander.controlify.controller.input.GamepadInputs.BACK_BUTTON)||state.isButtonDown(dev.isxander.controlify.controller.input.GamepadInputs.TOUCHPAD_1_BUTTON)){closeMap.run();return true;}
  keepCursor.run();return false;
 }
 /** Select closes the map before Controlify can treat it as a cursor toggle. */
 public static final class MapBackProcessor extends dev.isxander.controlify.screenop.ScreenProcessor<net.minecraft.class_437>{
  public MapBackProcessor(net.minecraft.class_437 screen){super(screen);}
  public dev.isxander.controlify.virtualmouse.VirtualMouseBehaviour virtualMouseBehaviour(){return dev.isxander.controlify.virtualmouse.VirtualMouseBehaviour.ENABLED;}
  public void onControllerUpdate(dev.isxander.controlify.controller.ControllerEntity controller){
   if(controller.input().isPresent()){var state=controller.input().get().rawStateNow();if(state.isButtonDown(dev.isxander.controlify.controller.input.GamepadInputs.BACK_BUTTON)||state.isButtonDown(dev.isxander.controlify.controller.input.GamepadInputs.TOUCHPAD_1_BUTTON))return;}
   var mouse=Controlify.instance().virtualMouseHandler();if(!mouse.isVirtualMouseEnabled())mouse.enableVirtualMouse();
   super.onControllerUpdate(controller);
  }
 }
 public static void configureSelectDefaults(dev.isxander.controlify.controller.input.InputComponent input){
  var back=new dev.isxander.controlify.bindings.input.ButtonInput(new class_2960("controlify:button/back"));
  for(String id:List.of("controlify:change_perspective","controlify:take_screenshot")){
   var binding=input.getBinding(new class_2960(id));
   if(binding!=null&&binding.boundInput().equals(back))binding.setBoundInput(dev.isxander.controlify.bindings.input.EmptyInput.INSTANCE);
  }
 }
 /** Decide tap versus hold once, without emitting a tap on press or after a hold. */
 public static final class SelectFlow{
  private SelectGesture gesture=new SelectGesture();private boolean waitRelease;
  public int update(boolean physical,boolean bound,boolean inMap,boolean inGame,long now){
   if(waitRelease){
    gesture.reset();
    if(!physical&&!bound){waitRelease=false;gesture=new SelectGesture();}
    return inMap?4:0;
   }
   if(inMap){
    gesture.reset();
    if(physical){waitRelease=true;return 3;}
    return 4;
   }
   return gesture.update(bound,inGame,now);
  }
 }
 public static final class SelectGesture{
  private boolean down,held,blocked;private long started;
  public void reset(){down=false;held=false;blocked=true;}
  public int update(boolean pressed,boolean allowed,long now){
   if(!allowed){reset();return 0;}
   if(blocked){if(!pressed)blocked=false;return 0;}
   if(pressed){
    if(!down){down=true;held=false;started=now;}
    if(!held&&now-started>=500_000_000L){held=true;return 2;}
    return 0;
   }
   if(!down)return 0;
   down=false;boolean screenshot=held||now-started>=500_000_000L;boolean already=held;held=false;
   return already?0:screenshot?2:1;
  }
 }

 /** Ignore the opening press; close on the next press and consume it through release. */
 public static final class PauseGesture{
  private boolean waitRelease;
  public PauseGesture(boolean openingPress){waitRelease=openingPress;}
  public boolean update(boolean pressed){
   if(waitRelease){if(!pressed)waitRelease=false;return false;}
   if(!pressed)return false;
   waitRelease=true;return true;
  }
 }
 /** Runs after input sampling, before Controlify's in-game pause action. */
 public static void filterPauseInput(dev.isxander.controlify.controller.ControllerEntity controller){
  if(!PAUSE_BLOCKED.contains(controller))return;
  boolean pressed=pauseButtonDown(controller);
  dev.isxander.controlify.bindings.ControlifyBindings.PAUSE.on(controller).pushState(dev.isxander.controlify.controller.input.ControllerState.EMPTY);
  if(!pressed)PAUSE_BLOCKED.remove(controller);
 }
 public static boolean pauseButtonDown(dev.isxander.controlify.controller.ControllerEntity controller){
  var input=controller.input().orElseThrow();
  return dev.isxander.controlify.bindings.ControlifyBindings.PAUSE.on(controller).boundInput().state(input.stateNow())>=input.confObj().buttonActivationThreshold;
 }
 /** Pause has a dedicated built-in processor, so handle this after Controlify's tick. */
 public static boolean handlePauseScreen(net.minecraft.class_310 mc,boolean pressed){
  if(!(mc.field_1755 instanceof net.minecraft.class_433 pause))return false;
  var gesture=PAUSE_GESTURES.computeIfAbsent(pause,key->new PauseGesture(pressed));
  if(!gesture.update(pressed))return false;
  Controlify.instance().getCurrentController().ifPresent(PAUSE_BLOCKED::add);
  PAUSE_GESTURES.remove(pause);pause.method_25419();return true;
 }
 /** Options' generic close resumes play; its Done button retains the parent. */
 public static final class OptionsBackProcessor extends dev.isxander.controlify.screenop.ScreenProcessor<net.minecraft.class_429>{
  public OptionsBackProcessor(net.minecraft.class_429 screen){super(screen);}
  public boolean backToParent(){
   var done=getWidget("gui.done").orElse(null);
   if(!(done instanceof net.minecraft.class_4185 button))return false;
   playClackSound();button.method_25306();return true;
  }
  protected void handleButtons(dev.isxander.controlify.controller.ControllerEntity controller){
   if(dev.isxander.controlify.bindings.ControlifyBindings.GUI_BACK.on(controller).guiPressed().get()&&backToParent())return;
   super.handleButtons(controller);
  }
 }
 /** Capture the actual caller while the child screen is being constructed. */
 public static class ParentBackProcessor extends dev.isxander.controlify.screenop.ScreenProcessor<net.minecraft.class_437>{
  private final net.minecraft.class_437 parent;
  public ParentBackProcessor(net.minecraft.class_437 screen){super(screen);parent=minecraft.field_1755;}
  public boolean backToParent(){
   playClackSound();
   if(parent!=null&&parent!=screen)minecraft.method_1507(parent);else screen.method_25419();
   return true;
  }
  protected void handleButtons(dev.isxander.controlify.controller.ControllerEntity controller){
   if(dev.isxander.controlify.bindings.ControlifyBindings.GUI_BACK.on(controller).guiPressed().get()&&backToParent())return;
   super.handleButtons(controller);
  }
 }
 /** Cancel must run the dialog callback, without following its external link. */
 public static final class CancelBackProcessor extends ParentBackProcessor{
  public CancelBackProcessor(net.minecraft.class_407 screen){super(screen);}
  public boolean backToParent(){
   var cancel=getWidget("gui.cancel").orElse(null);
   if(!(cancel instanceof net.minecraft.class_4185 button))return false;
   playClackSound();button.method_25306();return true;
  }
 }
}
