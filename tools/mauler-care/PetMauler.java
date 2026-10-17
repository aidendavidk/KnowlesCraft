package knowlescraft.mauler;
import java.util.UUID;
public interface PetMauler {
 UUID knowlescraft$owner();
 void knowlescraft$tame(UUID owner);
 void knowlescraft$petTick();
 net.minecraft.class_1657 knowlescraft$lure();
 boolean knowlescraft$returning();
 net.minecraft.class_243 knowlescraft$home();
 void knowlescraft$returned();
}
