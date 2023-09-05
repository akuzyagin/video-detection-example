package factories;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Singleton
public class DesktopVideoPanelFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DesktopVideoPanelFactory.class);

    private final ThreadLocal<DesktopPanel> threadLocal = new ThreadLocal<>();

    private static DetectedObjects convert(List<DetectedObjects.DetectedObject> items) {
        var classNames = items.stream().map(Classifications.Classification::getClassName).toList();
        var probabilities = items.stream().map(Classifications.Classification::getProbability).toList();
        var boundingBoxes = items.stream().map(DetectedObjects.DetectedObject::getBoundingBox).toList();

        return new DetectedObjects(classNames, probabilities, boundingBoxes);
    }

    public void init(String videoPath, int width, int height) {
        threadLocal.set(new DesktopPanel(videoPath, width, height));
    }

    public DesktopPanel get() {
        final var desktopPanel = threadLocal.get();
        if (Objects.isNull(desktopPanel)) {
            LOG.error("DesktopPanel not inited");
            throw new RuntimeException("DesktopPanel not inited!!!");
        }
        return desktopPanel;
    }

    public static class DesktopPanel {

        private final String videoPath;
        private final int width;
        private final int height;

        private JLabel videoPanel;

        public DesktopPanel(String videoPath, int width, int height) {
            this.videoPath = videoPath;
            this.width = width;
            this.height = height;
        }

        public void show(Image image, List<DetectedObjects.DetectedObject> items) {
            if (Objects.isNull(videoPanel)) {
                init();
            }
            try {
                image.drawBoundingBoxes(convert(items));

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                image.save(output, "jpg");
                byte[] data = output.toByteArray();
                ByteArrayInputStream input = new ByteArrayInputStream(data);
                BufferedImage img = ImageIO.read(input);
                ImageIcon videoPanelImage = new ImageIcon(img);
                videoPanel.setIcon(videoPanelImage);
                videoPanel.repaint();
            } catch (IOException ex) {
                LOG.error("Can't show an image");
                throw new RuntimeException("Can't show an image", ex);
            }
        }

        private void init() {
            JFrame jframe = new JFrame(videoPath);
            videoPanel = new JLabel();
            jframe.setContentPane(videoPanel);
            jframe.setSize(width, height);
            jframe.setUndecorated(false);
            jframe.setVisible(true);
            jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

    }

}
