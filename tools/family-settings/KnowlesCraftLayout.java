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
  var slotField=field(AbstractContainerScreenProcessor.class,"hoveredSlot");
  var slot=((java.util.function.Supplier<class_1735>)slotField.get(processor)).get();
  var container=(class_465<?>)screen;var accessor=(AbstractContainerScreenAccessor)screen;
  var mouse=Controlify.instance().virtualMouseHandler();
  var window=class_310.method_1551().method_22683();
  double x=mouse.getCurrentX(1)*(double)screen.field_22789/window.method_4480();
  double y=mouse.getCurrentY(1)*(double)screen.field_22790/window.method_4507();
  var held=container.method_17577().method_34255();
  var ctx=new dev.isxander.controlify.gui.guide.ContainerGuideCtx(slot,held,slot==null&&accessor.invokeHasClickedOutside(x,y,accessor.getLeftPos(),accessor.getTopPos(),0));
  for(var position:List.of(left,right))if(position.getComponent() instanceof ColumnLayoutComponent<?> column)
   for(var child:column.getChildComponents())if(child instanceof RowLayoutComponent<?> row)
    for(var action:row.getChildComponents())if(action instanceof GuideActionRenderer<?> guide)((GuideActionRenderer<dev.isxander.controlify.gui.guide.ContainerGuideCtx>)guide).updateName(ctx);
  // Slot acceptance alone does not mean two existing stacks can merge.
  if(!held.method_7960()&&slot!=null&&slot.method_7680(held)){
   var column=(ColumnLayoutComponent<?>)left.getComponent();
   var row=(RowLayoutComponent<?>)column.getChildComponents().get(0);
   boolean swap=slot.method_7681()&&!class_1799.method_31577(held,slot.method_7677());
   NAME.set(row.getChildComponents().get(0),class_2561.method_43471(swap?"controlify.guide.container.swap":held.method_7947()>1?"controlify.guide.container.place_all":"controlify.guide.container.place_one"));
  }
 }
 /** Recipe-book processors omit the container's held-stack drop action. */
 public static boolean handleRecipeDrop(class_437 screen,dev.isxander.controlify.controller.ControllerEntity controller){
  if(!(screen instanceof class_465<?> container)||container.method_17577().method_34255().method_7960()||!Controlify.instance().currentInputMode().isController())return false;
  try{
   var processor=containerProcessor(screen);
   if(processor==null||ScreenProcessorProvider.provide(screen)==processor)return false;
   if(!dev.isxander.controlify.bindings.ControlifyBindings.DROP_INVENTORY.on(controller).justPressed())return false;
   var click=(AbstractContainerScreenProcessor.ClickSlotFunction)field(AbstractContainerScreenProcessor.class,"clickSlotFunction").get(processor);
   click.clickSlot(null,-999,0,class_1713.field_7790);
   return true;
  }catch(Exception e){if(!warned){warned=true;System.err.println("[KnowlesCraft] Inventory drop unavailable: "+e);}return false;}
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
   var builder=ColumnLayoutComponent.<RowLayoutComponent<RenderComponent>>builder().spacing(0).colPadding(2).elementPosition(right?ColumnLayoutComponent.ElementPosition.RIGHT:ColumnLayoutComponent.ElementPosition.LEFT);
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
 private static void hideBottomButtons(){
  for(class_339 button:new class_339[]{EmiScreenManager.emi,EmiScreenManager.tree})if(button!=null){
   button.field_22764=false;button.field_22763=false;
  }
 }
 public static List<Bounds> layout(class_437 screen,boolean recalculate){
  hideBottomButtons();
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
   if(active)hideDuplicateDrop(pair);
   int primary=primaryHeight(pair);
   // Keep both hint rows on EMI's existing 18-unit item grid.
   var itemPanel=EmiScreenManager.getPanelFor(dev.emi.emi.config.SidebarSide.RIGHT);
   int gridTop=itemPanel!=null&&itemPanel.space!=null?itemPanel.space.ty:20;
   int gridBottom=gridTop+Math.max(1,(screen.field_22790-2-gridTop)/18)*18;
   int bottom=gridBottom-primary+5;
   pair.normalLeft.updatePosition(screen.field_22789,bottom+primary-2);placeRight(pair.normalRight,screen.field_22789,bottom,primary);
   int x=((AbstractContainerScreenAccessor)container).getLeftPos();
   int top=((AbstractContainerScreenAccessor)container).getTopPos();
   int width=WIDTH.getInt(container);
   boolean compact=overlapsContainer(bounds(pair.normalLeft,gridTop),x,top,width,HEIGHT.getInt(container))||overlapsContainer(bounds(pair.normalRight,gridTop),x,top,width,HEIGHT.getInt(container));
   var nextLeft=active?(compact?pair.compactLeft:pair.normalLeft):pair.originalLeft;
   var nextRight=active?(compact?pair.compactRight:pair.normalRight):pair.originalRight;
   var renderables=((ScreenAccessor)screen).getRenderables();
   // Draw once through the Fabric overlay, including recipe-book render paths.
   for(var guide:List.of(left,right,pair.originalLeft,pair.originalRight,pair.normalLeft,pair.normalRight,pair.compactLeft,pair.compactRight))renderables.removeIf(r->r==guide);
   LEFT.set(processor,nextLeft);RIGHT.set(processor,nextRight);
   nextLeft.updatePosition(screen.field_22789,active?bottom+primary-2:screen.field_22790);
   if(active)placeRight(nextRight,screen.field_22789,bottom,primary);else nextRight.updatePosition(screen.field_22789,screen.field_22790);
   var bounds=new ArrayList<Bounds>();
   if(active&&!visibleActions(nextLeft).isEmpty())bounds.add(bounds(nextLeft,gridTop));
   if(active&&!visibleActions(nextRight).isEmpty())bounds.add(bounds(nextRight,gridTop));
   var previous=BOUNDS.put(screen,bounds);
   if(recalculate&&!bounds.equals(previous))EmiScreenManager.recalculate();
   return bounds;
  }catch(Exception e){if(!warned){warned=true;System.err.println("[KnowlesCraft] Inventory guide layout unavailable: "+e);}return List.of();}
 }
 private static Bounds bounds(PositionedComponent<?> position,int gridTop){
  var size=position.getComponent().size();
  return new Bounds(position.x()-2,position.y()+4,size.x()+4,Math.max(0,size.y()-10));
 }
}
