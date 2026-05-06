package com.wardiusz.Pandus.commands.autocmd;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;

public class WelcomeCard {
    public static byte[] createCard(String avatarUrl, String displayName, int memberIndex) throws IOException {
        final int width = 1100;
        final int height = 500;
        final int cornerArc = 25;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // HIGH QUALITY
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fill background (dark gray / near black)
        g.setColor(new Color(10, 10, 10));
        g.fillRoundRect(0, 0, width, height, cornerArc, cornerArc);

        // Slight inner subtle border to match sample
        g.setColor(new Color(255, 255, 255, 14));
        g.setStroke(new BasicStroke(60f));
        g.drawRoundRect(2, 2, width - 4, height - 4, cornerArc, cornerArc);

        // ---------- Avatar area (center top) ----------
        // Avatar diameter and position (centered horizontally, higher on canvas)
        final int avatarDiameter = 240;            // big circle
        final int avatarX = (width - avatarDiameter) / 2;
        final int avatarY = 60;                    // top padding

        // Load avatar with fallback
        BufferedImage avatar;
        try {
            avatar = ImageIO.read(new URL(avatarUrl));
        } catch (Exception e) {
            // fallback placeholder (solid gray)
            avatar = new BufferedImage(avatarDiameter, avatarDiameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gph = avatar.createGraphics();
            gph.setColor(new Color(120, 120, 120));
            gph.fillRect(0, 0, avatar.getWidth(), avatar.getHeight());
            gph.dispose();
        }

        // Create circular clipped avatar at avatarDiameter size
        BufferedImage clipped = new BufferedImage(avatarDiameter, avatarDiameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gClip = clipped.createGraphics();
        gClip.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Circular clip
        Ellipse2D circle = new Ellipse2D.Double(0, 0, avatarDiameter, avatarDiameter);
        gClip.setClip(circle);

        // Draw avatar scaled to fill the circle (cover behavior)
        gClip.drawImage(avatar, 0, 0, avatarDiameter, avatarDiameter, null);
        gClip.dispose();

        // draw small shadow under avatar (offset + blur-like)
        g.setColor(new Color(0, 0, 0, 120));
        g.fillOval(avatarX + 6, avatarY + avatarDiameter + 6, avatarDiameter - 10, 20);

        // Draw a white outer ring (thick)
        int outerStroke = 8;
        g.setColor(new Color(255, 255, 255,255));
        g.setStroke(new BasicStroke(outerStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(avatarX - outerStroke/2, avatarY - outerStroke/2, avatarDiameter + outerStroke, avatarDiameter + outerStroke);

        // Draw inner subtle dark ring to match sample
        int innerStroke = 6;
        g.setColor(new Color(20, 20, 20, 255)); // same as background-ish
        g.setStroke(new BasicStroke(innerStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int innerOffset = (outerStroke/2) + 2;
        g.drawOval(avatarX + innerOffset, avatarY + innerOffset, avatarDiameter - innerOffset*2 + innerStroke/2, avatarDiameter - innerOffset*2 + innerStroke/2);

        // Draw the clipped avatar on top (so rings look consistent)
        g.drawImage(clipped, avatarX, avatarY, null);

        // ---------- Text area (centered below avatar) ----------
        // Username
        String username = displayName;
        String subtitle = "just joined the server";
        String memberLine = "Member #" + memberIndex;

        // Fonts
        Font usernameFont = new Font("SansSerif", Font.PLAIN, 52);
        Font subtitleFont = new Font("SansSerif", Font.PLAIN, 32);
        Font memberFont = new Font("SansSerif", Font.PLAIN, 28);

        // Centering baseline positions
        int centerX = width / 2;
        int usernameY = avatarY + avatarDiameter + 75; // move below avatar
        int subtitleY = usernameY + 44;
        int memberY = subtitleY + 33;

        // Draw username
        g.setFont(usernameFont);
        g.setColor(Color.WHITE);
        drawCenteredString(g, username, centerX, usernameY);

        // Draw subtitle
        g.setFont(subtitleFont);
        g.setColor(new Color(200, 200, 200));
        drawCenteredString(g, subtitle, centerX, subtitleY);

        // Draw member number
        g.setFont(memberFont);
        g.setColor(new Color(160, 160, 160));
        drawCenteredString(g, memberLine, centerX, memberY);

        // Finish
        g.dispose();

        // Output PNG bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        return baos.toByteArray();
    }


    // Draw a string horizontally centered at (x, y baseline)
    private static void drawCenteredString(Graphics2D g, String text, int centerX, int baselineY) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int ascent = fm.getAscent();
        // Baseline Y is baseline, so we place text baseline at baselineY
        g.drawString(text, centerX - textWidth / 2, baselineY + (ascent/2 - fm.getHeight()/2));
    }

    public static void createAndSendWelcomeCard(GuildMemberJoinEvent event, String greeting, TextChannel channel, int memberIndex) {
        Member member = event.getMember();
        User user = member.getUser();

        String avatarUrl = user.getEffectiveAvatarUrl();
        String displayName = member.getEffectiveName();

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                byte[] png = createCard(avatarUrl, displayName, memberIndex);
                try {
                    channel.sendMessage(greeting).addFiles(FileUpload.fromData(png, "welcome.png")).queue();
                } catch (NoClassDefFoundError | NoSuchMethodError e) {
                    e.printStackTrace();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
