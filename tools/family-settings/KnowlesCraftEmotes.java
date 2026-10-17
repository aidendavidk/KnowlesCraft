package family.settings;
import net.minecraft.*;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.api.bind.*;
import dev.isxander.controlify.gui.screen.RadialMenuScreen;
import com.yamikhal.playeremotes.client.PlayerEmotesClient;
import com.yamikhal.playeremotes.client.emote.*;
import com.yamikhal.playeremotes.client.preview.*;
import com.yamikhal.playeremotes.client.gui.Canvas;
import java.util.*;

/** The same tap/hold gesture as the utility wheel, with native emote playback. */
public final class KnowlesCraftEmotes {
 public static void cancelForAttack(){
  var player=class_310.method_1551().field_1724;if(player==null)return;
  var active=com.yamikhal.playeremotes.client.animation.EmotePlayers.get(player.method_5667());
  if(active!=null&&!active.isStopping())PlayerEmotesClient.stop();
 }
 private static boolean pendingLibrary;
 public static void requestLibrary(){pendingLibrary=true;}
 public static void flushLibrary(class_310 mc){
  if(!pendingLibrary)return;pendingLibrary=false;
  if(mc.field_1755==null)mc.method_1507(new com.yamikhal.playeremotes.client.gui.WheelEditScreen(null));
 }

 private final KnowlesCraftRadial.Gesture gesture=new KnowlesCraftRadial.Gesture();
 private static final String[] DEFAULTS={"wave","clap","cheer","laugh","facepalm","sit_rest","robot_dance","bow"};
 public static void seedFavorites(){
  var config=PlayerEmotesClient.config();boolean empty=true;
  for(int i=0;i<8;i++)if(config.wheelSlot(0,i)!=null)empty=false;
  if(!empty)return;
  for(int i=0;i<8;i++)config.setWheelSlot(0,i,new class_2960("playeremotes",DEFAULTS[i]));config.save();
 }
 public static RadialMenuScreen.RadialItem[] items(){
  seedFavorites();var items=new RadialMenuScreen.RadialItem[8];
  for(int i=0;i<8;i++){
   var id=PlayerEmotesClient.config().wheelSlot(0,i);var emote=id==null?null:EmoteRegistry.get(id);
   items[i]=new RadialMenuScreen.RadialItem(){
    public class_2561 name(){return emote==null?class_2561.method_43470("Empty emote slot"):emote.name();}
    public RadialIcon icon(){return RadialIcon.EMPTY;}
    public boolean playAction(){if(emote==null)return false;PlayerEmotesClient.play(emote);return true;}
   };
  }
  return items;
 }
 public static final class Screen extends RadialMenuScreen {
  public Screen(ControllerEntity controller,InputBinding binding){super(controller,binding,items(),class_2561.method_43470("Emotes"),null,null);}
  @Override public void method_25394(class_332 graphics,int mouseX,int mouseY,float delta){
   if(class_310.method_1551().field_1687==null)graphics.method_25294(0,0,field_22789,field_22790,0xff202020);
   super.method_25394(graphics,mouseX,mouseY,delta);
   int slot=0;
   for(var child:method_25396())if(child instanceof RadialMenuScreen.RadialButton button&&slot<8){
    var id=PlayerEmotesClient.config().wheelSlot(0,slot++);var emote=id==null?null:EmoteRegistry.get(id);
    if(emote==null)continue;var frame=EmotePreview.frame(emote,button,true);
    if(frame!=null)PreviewRenderer.draw(new Canvas(graphics),frame,button.getX()+2,button.getY()+1,button.getX()+30,button.getY()+30);
   }
  }
 }
 public void tick(class_310 mc,ControllerEntity controller,InputBinding binding){
  var input=controller.input().orElseThrow();
  boolean down=binding.boundInput().state(input.rawStateNow())>=input.confObj().buttonActivationThreshold;
  if(mc.field_1755 instanceof RadialMenuScreen)return;
  int action=gesture.update(down,mc.field_1755==null&&mc.field_1724!=null,System.nanoTime());
  if(action==2){mc.method_1507(new Screen(controller,binding));return;}
  if(action==1){
   var recent=PlayerEmotesClient.config().recent;
   if(!recent.isEmpty()){var emote=EmoteRegistry.get(recent.get(0));if(emote!=null){PlayerEmotesClient.play(emote);return;}}
   mc.field_1724.method_7353(class_2561.method_43470("Hold D-pad Left to choose an emote"),true);
  }
 }
}
