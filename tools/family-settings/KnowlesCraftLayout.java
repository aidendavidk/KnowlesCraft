package family.settings;

import java.lang.reflect.Field;
import java.util.*;
import net.minecraft.*;
import dev.isxander.controlify.Controlify;
import dev.isxander.controlify.gui.layout.*;
import dev.isxander.controlify.screenop.*;
import dev.isxander.controlify.screenop.compat.vanilla.AbstractContainerScreenProcessor;
import dev.isxander.controlify.mixins.feature.guide.screen.AbstractContainerScreenAccessor;
import dev.isxander.controlify.mixins.feature.screenop.ScreenAccessor;
import dev.emi.emi.api.*;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.screen.EmiScreenManager;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

/** Reserve actual guide bounds in EMI instead of drawing two interfaces together. */
public final class KnowlesCraftLayout implements EmiPlugin {
 private static final Field LEFT=field(AbstractContainerScreenProcessor.class,"leftLayout"),RIGHT=field(AbstractContainerScreenProcessor.class,"rightLayout"),HEIGHT=field(class_465.class,"field_2779");
 private static final Map<ScreenProcessor<?>,Pair> PAIRS=new WeakHashMap<>();
 private static final Map<class_437,List<Bounds>> BOUNDS=new WeakHashMap<>();
 private static boolean installed,warned;
 private record Pair(PositionedComponent<?> originalLeft,PositionedComponent<?> originalRight,PositionedComponent<?> normalLeft,PositionedComponent<?> normalRight,PositionedComponent<?> compactLeft,PositionedComponent<?> compactRight){}
 private static Field field(Class<?> type,String name){try{var f=type.getDeclaredField(name);f.setAccessible(true);return f;}catch(Exception e){throw new IllegalStateException("Controller layout API changed",e);}}
 public void register(EmiRegistry registry){registry.addGenericExclusionArea((screen,out)->layout(screen,false).forEach(out));}
 public static void install(){
  if(installed)return;installed=true;
  ScreenEvents.AFTER_INIT.register((mc,screen,width,height)->ScreenEvents.beforeRender(screen).register((s,context,x,y,delta)->layout(s,true)));
 }
 @SuppressWarnings({"rawtypes","unchecked"})
 private static PositionedComponent<?> position(PositionedComponent<?> original,boolean right,boolean compact){
  RenderComponent component=original.getComponent();
  if(compact&&component instanceof ColumnLayoutComponent column){
   var builder=ColumnLayoutComponent.<RowLayoutComponent<RenderComponent>>builder().spacing(2).colPadding(2).elementPosition(right?ColumnLayoutComponent.ElementPosition.RIGHT:ColumnLayoutComponent.ElementPosition.LEFT);
   for(Object child:column.getChildComponents())if(child instanceof RowLayoutComponent row)for(Object action:row.getChildComponents())builder.element(RowLayoutComponent.<RenderComponent>builder().rowPadding(0).elementPosition(RowLayoutComponent.ElementPosition.MIDDLE).element((RenderComponent)action).build());
   component=builder.build();
  }
  var anchor=right?AnchorPoint.BOTTOM_RIGHT:AnchorPoint.BOTTOM_LEFT;
  return new PositionedComponent<>(component,anchor,right?-4:4,0,anchor);
 }
 /** Return a row bottom, or -1 when a tall container needs side columns. */
 public static int rowBottom(int toolbarTop,int guideHeight,int inventoryTop,int inventoryBottom){
  int bottom=toolbarTop-4;
  if(bottom-guideHeight>=inventoryBottom+4)return bottom;
  if(inventoryTop-guideHeight>=8)return inventoryTop-4;
  return -1;
 }
 public static List<Bounds> layout(class_437 screen,boolean recalculate){
  if(!(screen instanceof class_465<?> container))return List.of();
  try{
   var processor=ScreenProcessorProvider.provide(screen);
   if(!(processor instanceof AbstractContainerScreenProcessor<?>))return List.of();
   var left=(PositionedComponent<?>)LEFT.get(processor);var right=(PositionedComponent<?>)RIGHT.get(processor);
   if(left==null||right==null)return List.of();
   var pair=PAIRS.get(processor);
   if(pair==null||left!=pair.originalLeft&&left!=pair.normalLeft&&left!=pair.compactLeft){
    pair=new Pair(left,right,position(left,false,false),position(right,true,false),position(left,false,true),position(right,true,true));PAIRS.put(processor,pair);
   }
   boolean active=Controlify.instance()!=null&&Controlify.instance().currentInputMode().isController()&&!EmiScreenManager.isDisabled();
   int toolbar=screen.field_22790;
   for(class_339 widget:new class_339[]{EmiScreenManager.search,EmiScreenManager.emi,EmiScreenManager.tree})if(widget!=null&&widget.field_22764&&widget.method_46427()>screen.field_22790/2)toolbar=Math.min(toolbar,widget.method_46427());
   active&=toolbar<screen.field_22790;
   int height=Math.max(pair.normalLeft.getComponent().size().y(),pair.normalRight.getComponent().size().y());
   int top=((AbstractContainerScreenAccessor)container).getTopPos();
   int bottom=rowBottom(toolbar,height,top,top+HEIGHT.getInt(container));
   boolean compact=bottom<0; if(compact)bottom=toolbar-4;
   var nextLeft=active?(compact?pair.compactLeft:pair.normalLeft):pair.originalLeft;
   var nextRight=active?(compact?pair.compactRight:pair.normalRight):pair.originalRight;
   var renderables=((ScreenAccessor)screen).getRenderables();
   int li=renderables.indexOf(left),ri=renderables.indexOf(right);
   if(li>=0)renderables.set(li,nextLeft);if(ri>=0)renderables.set(ri,nextRight);
   LEFT.set(processor,nextLeft);RIGHT.set(processor,nextRight);
   nextLeft.updatePosition(screen.field_22789,active?bottom:screen.field_22790);nextRight.updatePosition(screen.field_22789,active?bottom:screen.field_22790);
   List<Bounds> bounds=active?List.of(bounds(nextLeft),bounds(nextRight)):List.of();
   var previous=BOUNDS.put(screen,bounds);
   if(recalculate&&!bounds.equals(previous))EmiScreenManager.recalculate();
   return bounds;
  }catch(Exception e){if(!warned){warned=true;System.err.println("[KnowlesCraft] Inventory guide layout unavailable: "+e);}return List.of();}
 }
 private static Bounds bounds(PositionedComponent<?> position){var size=position.getComponent().size();return new Bounds(position.x()-2,position.y()-2,size.x()+4,size.y()+4);}
}
