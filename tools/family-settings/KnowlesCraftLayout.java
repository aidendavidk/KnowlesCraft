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
import dev.isxander.controlify.gui.guide.GuideActionRenderer;
import dev.emi.emi.api.*;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.screen.EmiScreenManager;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

/** Reserve actual guide bounds in EMI instead of drawing two interfaces together. */
public final class KnowlesCraftLayout implements EmiPlugin {
 private static final Field LEFT=field(AbstractContainerScreenProcessor.class,"leftLayout"),RIGHT=field(AbstractContainerScreenProcessor.class,"rightLayout"),WIDTH=field(class_465.class,"field_2792"),HEIGHT=field(class_465.class,"field_2779"),NAME=field(GuideActionRenderer.class,"name");
 private static final Map<ScreenProcessor<?>,Pair> PAIRS=new WeakHashMap<>();
 private static final Map<class_437,List<Bounds>> BOUNDS=new WeakHashMap<>();
 private static boolean installed,warned;
 private record Pair(PositionedComponent<?> originalLeft,PositionedComponent<?> originalRight,PositionedComponent<?> normalLeft,PositionedComponent<?> normalRight,PositionedComponent<?> compactLeft,PositionedComponent<?> compactRight){}
 private static Field field(Class<?> type,String name){try{var f=type.getDeclaredField(name);f.setAccessible(true);return f;}catch(Exception e){throw new IllegalStateException("Controller layout API changed",e);}}
 public void register(EmiRegistry registry){registry.addGenericExclusionArea((screen,out)->layout(screen,false).forEach(out));}
 public static void install(){
  if(installed)return;installed=true;
  ScreenEvents.AFTER_INIT.register((mc,screen,width,height)->{
   ScreenEvents.beforeRender(screen).register((s,context,x,y,delta)->layout(s,true));

  });
 }
 private static AbstractContainerScreenProcessor<?> containerProcessor(class_437 screen)throws IllegalAccessException{
  var provided=ScreenProcessorProvider.provide(screen);
  if(provided instanceof AbstractContainerScreenProcessor<?> container)return container;
  // RecipeBookScreenProcessor replaces the provider, but the inherited container
  // processor still holds the slot guide definitions. Do not run its input handler.
  for(Class<?> type=screen.getClass();type!=null;type=type.getSuperclass())for(var f:type.getDeclaredFields()){
   if(AbstractContainerScreenProcessor.class.isAssignableFrom(f.getType())){
    f.setAccessible(true);return (AbstractContainerScreenProcessor<?>)f.get(screen);
   }
  }
  return null;
 }
 @SuppressWarnings("unchecked")
 private static void updateRecipeGuides(class_437 screen,AbstractContainerScreenProcessor<?> processor,PositionedComponent<?> left,PositionedComponent<?> right)throws Exception{
  if(ScreenProcessorProvider.provide(screen)==processor)return;
  var slotField=field(AbstractContainerScreenProcessor.class,"hoveredSlot");
  var slot=((java.util.function.Supplier<class_1735>)slotField.get(processor)).get();
  var container=(class_465<?>)screen;var accessor=(AbstractContainerScreenAccessor)screen;
  var mouse=Controlify.instance().virtualMouseHandler();
  var ctx=new dev.isxander.controlify.gui.guide.ContainerGuideCtx(slot,container.method_17577().method_34255(),accessor.invokeHasClickedOutside(mouse.getCurrentX(1),mouse.getCurrentY(1),accessor.getLeftPos(),accessor.getTopPos(),0));
  for(var position:List.of(left,right))if(position.getComponent() instanceof ColumnLayoutComponent<?> column)
   for(var child:column.getChildComponents())if(child instanceof RowLayoutComponent<?> row)
    for(var action:row.getChildComponents())if(action instanceof GuideActionRenderer<?> guide)((GuideActionRenderer<dev.isxander.controlify.gui.guide.ContainerGuideCtx>)guide).updateName(ctx);
 }
 private static boolean guidesEnabled(){
  var api=Controlify.instance();
  return api!=null&&api.currentInputMode().isController()&&api.getCurrentController().map(c->c.genericConfig().config().showScreenGuides).orElse(false);
 }
 public static void renderGuides(class_437 screen,class_332 context,float delta){
  if(!(screen instanceof class_465<?>)||!guidesEnabled())return;
  try{
   var processor=containerProcessor(screen);
   if(processor==null)return;
   var left=(PositionedComponent<?>)LEFT.get(processor);var right=(PositionedComponent<?>)RIGHT.get(processor);
   if(left!=null)left.renderComponent(context,delta);
   if(right!=null)right.renderComponent(context,delta);
  }catch(Exception e){if(!warned){warned=true;System.err.println("[KnowlesCraft] Controller hints unavailable: "+e);}}
 }
 @SuppressWarnings({"rawtypes","unchecked"})
 private static PositionedComponent<?> position(PositionedComponent<?> original,boolean right,boolean compact){
  RenderComponent component=original.getComponent();
  if(right&&!compact&&component instanceof ColumnLayoutComponent column){
   var rows=new ArrayList<>(column.getChildComponents());Collections.reverse(rows);
   var builder=ColumnLayoutComponent.<RenderComponent>builder().spacing(2).colPadding(2).elementPosition(ColumnLayoutComponent.ElementPosition.RIGHT);
   for(Object row:rows)builder.element((RenderComponent)row);component=builder.build();
  }
  if(compact&&component instanceof ColumnLayoutComponent column){
   var builder=ColumnLayoutComponent.<RowLayoutComponent<RenderComponent>>builder().spacing(2).colPadding(2).elementPosition(right?ColumnLayoutComponent.ElementPosition.RIGHT:ColumnLayoutComponent.ElementPosition.LEFT);
   var rows=new ArrayList<>(column.getChildComponents());
   for(Object child:rows)if(child instanceof RowLayoutComponent row){
    var actions=new ArrayList<>(row.getChildComponents());if(right)Collections.reverse(actions);
    for(Object action:actions)builder.element(RowLayoutComponent.<RenderComponent>builder().rowPadding(0).elementPosition(RowLayoutComponent.ElementPosition.MIDDLE).element((RenderComponent)action).build());
   }
   component=builder.build();
  }
  var anchor=right?AnchorPoint.BOTTOM_RIGHT:AnchorPoint.BOTTOM_LEFT;
  return new PositionedComponent<>(component,anchor,right?-4:4,0,anchor);
 }
 private static List<GuideActionRenderer<?>> visibleActions(PositionedComponent<?> position){
  var result=new ArrayList<GuideActionRenderer<?>>();
  if(position.getComponent() instanceof ColumnLayoutComponent<?> column)for(Object child:column.getChildComponents())if(child instanceof RowLayoutComponent<?> row)for(Object action:row.getChildComponents())if(action instanceof GuideActionRenderer<?> guide&&guide.isVisible())result.add(guide);
  return result;
 }
 private static void hideDuplicateDrop(Pair pair)throws IllegalAccessException{
  String drop=class_2561.method_43471("controlify.guide.container.drop").getString();
  boolean onLeft=false;
  for(var guide:visibleActions(pair.normalLeft))if(((class_2561)NAME.get(guide)).getString().equals(drop))onLeft=true;
  if(onLeft)for(var guide:visibleActions(pair.normalRight))if(((class_2561)NAME.get(guide)).getString().equals(drop))NAME.set(guide,null);
 }
 private static int primaryHeight(Pair pair){
  int height=0;
  for(var guide:visibleActions(pair.normalLeft))height=Math.max(height,guide.size().y());
  var column=(ColumnLayoutComponent<?>)pair.normalRight.getComponent();
  if(!column.getChildComponents().isEmpty())height=Math.max(height,column.getChildComponents().get(0).size().y());
  return height+4;
 }
 private static void placeRight(PositionedComponent<?> right,int width,int bottom,int primaryHeight){
  // Quick Move stays on the lower row; hidden actions free the space above it.
  right.updatePosition(width,bottom+primaryHeight-2);
 }
 /** Side guides may share the inventory's vertical space without covering its slots. */
 public static boolean overlapsContainer(Bounds guide,int left,int top,int width,int height){
  return guide.left()<left+width+4&&guide.right()>left-4&&guide.top()<top+height+4&&guide.bottom()>top-4;
 }
 public static List<Bounds> layout(class_437 screen,boolean recalculate){
  if(!(screen instanceof class_465<?> container))return List.of();
  try{
   var processor=containerProcessor(screen);
   if(processor==null)return List.of();
   var left=(PositionedComponent<?>)LEFT.get(processor);var right=(PositionedComponent<?>)RIGHT.get(processor);
   if(left==null||right==null){
    if(!guidesEnabled())return List.of();
    processor.onWidgetRebuild();left=(PositionedComponent<?>)LEFT.get(processor);right=(PositionedComponent<?>)RIGHT.get(processor);
    if(left==null||right==null)return List.of();
   }
   if(guidesEnabled())updateRecipeGuides(screen,processor,left,right);
   var pair=PAIRS.get(processor);
   if(pair==null||left!=pair.originalLeft&&left!=pair.normalLeft&&left!=pair.compactLeft){
    pair=new Pair(left,right,position(left,false,true),position(right,true,true),position(left,false,true),position(right,true,true));PAIRS.put(processor,pair);
   }
   boolean active=guidesEnabled();
   int toolbar=screen.field_22790;
   for(class_339 widget:new class_339[]{EmiScreenManager.search,EmiScreenManager.emi,EmiScreenManager.tree})if(widget!=null&&widget.field_22764&&widget.method_46427()>screen.field_22790/2)toolbar=Math.min(toolbar,widget.method_46427());

   if(active)hideDuplicateDrop(pair);
   int bottom=toolbar-4,primary=primaryHeight(pair);
   pair.normalLeft.updatePosition(screen.field_22789,bottom);placeRight(pair.normalRight,screen.field_22789,bottom,primary);
   int x=((AbstractContainerScreenAccessor)container).getLeftPos();
   int top=((AbstractContainerScreenAccessor)container).getTopPos();
   int width=WIDTH.getInt(container);
   boolean compact=overlapsContainer(bounds(pair.normalLeft),x,top,width,HEIGHT.getInt(container))||overlapsContainer(bounds(pair.normalRight),x,top,width,HEIGHT.getInt(container));
   var nextLeft=active?(compact?pair.compactLeft:pair.normalLeft):pair.originalLeft;
   var nextRight=active?(compact?pair.compactRight:pair.normalRight):pair.originalRight;
   var renderables=((ScreenAccessor)screen).getRenderables();
   // Draw once through the Fabric overlay, including recipe-book render paths.
   for(var guide:List.of(left,right,pair.originalLeft,pair.originalRight,pair.normalLeft,pair.normalRight,pair.compactLeft,pair.compactRight))renderables.removeIf(r->r==guide);
   LEFT.set(processor,nextLeft);RIGHT.set(processor,nextRight);
   nextLeft.updatePosition(screen.field_22789,active?bottom:screen.field_22790);
   if(active)placeRight(nextRight,screen.field_22789,bottom,primary);else nextRight.updatePosition(screen.field_22789,screen.field_22790);
   var bounds=new ArrayList<Bounds>();
   if(active&&!visibleActions(nextLeft).isEmpty())bounds.add(bounds(nextLeft));
   if(active&&!visibleActions(nextRight).isEmpty())bounds.add(bounds(nextRight));
   var previous=BOUNDS.put(screen,bounds);
   if(recalculate&&!bounds.equals(previous))EmiScreenManager.recalculate();
   return bounds;
  }catch(Exception e){if(!warned){warned=true;System.err.println("[KnowlesCraft] Inventory guide layout unavailable: "+e);}return List.of();}
 }
 private static Bounds bounds(PositionedComponent<?> position){var size=position.getComponent().size();return new Bounds(position.x()-2,position.y()-2,size.x()+4,size.y()+4);}
}
