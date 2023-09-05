import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.translate.TranslateException;
import constant.Synset;
import factories.DesktopVideoPanelFactory;
import factories.YoloPredictorFactory;
import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.opencv.core.Mat;
import org.opencv.videoio.Videoio;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Startup
@Singleton
public class DetectionService {

    @Inject
    OpenCvService openCvService;
    @Inject
    YoloPredictorFactory yoloPredictorFactory;
    @Inject
    DesktopVideoPanelFactory videoPanelFactory;

    @ConfigProperty(name = "video.path")
    String videoPath;

    @PostConstruct
    public void detect() throws TranslateException {
        try (var predictor = yoloPredictorFactory.generate()) {
            final var camera = openCvService.capture(videoPath);

            final var width = (int) Math.ceil(camera.get(Videoio.CAP_PROP_FRAME_WIDTH));
            final var height = (int) Math.ceil(camera.get(Videoio.CAP_PROP_FRAME_HEIGHT));
            videoPanelFactory.init(videoPath, width, height);

            Mat frame = new Mat();
            while (camera.read(frame)) {
                if (!frame.empty()) {
                    Image image = ImageFactory.getInstance().fromImage(frame);
                    //TODO crop image by polygon before detect objects
                    DetectedObjects predict = predictor.predict(image);
                    List<DetectedObjects.DetectedObject> allItems = predict.items();
                    var filteredItems = allItems.stream()
                          .filter(detected -> Synset.isUsed(detected.getClassName()))
                          .toList();

                    final var listToShow = new ArrayList<>(filteredItems);
                    videoPanelFactory.get().show(image, listToShow);
                }
            }
        }

    }

}
