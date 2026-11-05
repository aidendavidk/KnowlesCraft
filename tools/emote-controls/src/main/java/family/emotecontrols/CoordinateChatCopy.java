package family.emotecontrols;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Makes coordinate triples in chat messages one-click copyable. */
final class CoordinateChatCopy {
    private static final Pattern COORDINATES = Pattern.compile(
            "(?i)(?:x\\s*[:=]\\s*)?(-?\\d+(?:\\.\\d+)?)[,\\s]+"
                    + "(?:y\\s*[:=]\\s*)?(-?\\d+(?:\\.\\d+)?)[,\\s]+"
                    + "(?:z\\s*[:=]\\s*)?(-?\\d+(?:\\.\\d+)?)(?![\\w.])");

    private CoordinateChatCopy() {
    }

    static void initialize() {
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) ->
                overlay ? message : makeCoordinatesClickable(message));

        // Fabric does not expose a modify event for signed player chat. Re-add only
        // coordinate-bearing messages with their usual chat decoration after adding
        // the client-side copy action.
        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, parameters, timestamp) -> {
            Text modified = makeCoordinatesClickable(message);
            if (modified == message) {
                return true;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.inGameHud == null) {
                return true;
            }
            client.inGameHud.getChatHud().addMessage(parameters.applyChatDecoration(modified));
            return false;
        });
    }

    private static Text makeCoordinatesClickable(Text message) {
        String plain = message.getString();
        Matcher matcher = COORDINATES.matcher(plain);
        if (!matcher.find()) {
            return message;
        }

        Style baseStyle = message.getStyle();
        Style clickableStyle = baseStyle
                .withColor(Formatting.AQUA)
                .withUnderline(true)
                .withClickEvent(new ClickEvent(
                        ClickEvent.Action.COPY_TO_CLIPBOARD,
                        matcher.group(1) + " " + matcher.group(2) + " " + matcher.group(3)))
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.literal("Click to copy coordinates")));

        MutableText result = Text.empty().setStyle(baseStyle);
        int cursor = 0;
        do {
            if (matcher.start() > cursor) {
                result.append(Text.literal(plain.substring(cursor, matcher.start())).setStyle(baseStyle));
            }
            result.append(Text.literal(plain.substring(matcher.start(), matcher.end())).setStyle(clickableStyle));
            cursor = matcher.end();
        } while (matcher.find());

        if (cursor < plain.length()) {
            result.append(Text.literal(plain.substring(cursor)).setStyle(baseStyle));
        }
        return result;
    }
}
