import io.quarkus.runtime.Startup;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Startup
@Singleton
public class OpenCvService {

    private static final Logger LOG = LoggerFactory.getLogger(OpenCvService.class);

    public VideoCapture capture(String path) {
        VideoCapture camera = new VideoCapture(path, Videoio.CAP_FFMPEG);
        if (!camera.isOpened()) {
            throw new RuntimeException("Can't open " + path);
        }

        LOG.info("Camera path {}", path);
        LOG.info("FPS {}", camera.get(Videoio.CAP_PROP_FPS));
        LOG.info("Frame count {}", camera.get(Videoio.CAP_PROP_FRAME_COUNT));
        LOG.info("Frame width {}", camera.get(Videoio.CAP_PROP_FRAME_WIDTH));
        LOG.info("Frame height {}", camera.get(Videoio.CAP_PROP_FRAME_HEIGHT));

        return camera;
    }

}
