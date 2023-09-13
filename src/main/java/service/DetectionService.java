package service;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.translate.TranslateException;
import constant.Synset;
import factories.DesktopVideoPanelFactory;
import factories.YoloPredictorFactory;
import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
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
                    Image croppedImage = getCroppedImage(frame);
                    DetectedObjects predict = predictor.predict(croppedImage);
                    List<DetectedObjects.DetectedObject> allItems = predict.items();
                    var filteredItems = allItems.stream()
                          .filter(detected -> Synset.isUsed(detected.getClassName()))
                          .toList();

                    videoPanelFactory.get().show(croppedImage, filteredItems);
                }
            }
        }

    }

    private static Image getCroppedImage(Mat frame) {
        // create polygon with coordinates for mask
        MatOfPoint points = new MatOfPoint(
              new Point(100, 400),
              new Point(500, 350),
              new Point(700, 300),
              new Point(1000, 500),
              new Point(1000, 800),
              new Point(700, 800),
              new Point(500, 800),
              new Point(100, 400)
        );

        // draw shape with size of frame from original video and black fill
        final Scalar colorBlack = new Scalar(0, 0, 0);
        Mat mask = new Mat(frame.rows(), frame.cols(), CvType.CV_8UC3, colorBlack);

        // fill polygon of white color on the black fill
        final Scalar colorWhite = new Scalar(255, 255, 255);
        Imgproc.fillPoly(mask, Collections.singletonList(points), colorWhite, Imgproc.LINE_8);

        // compute the bitwise_and of the frame and mask
        Mat result = new Mat();
        Core.bitwise_and(frame, mask, result);

        return ImageFactory.getInstance().fromImage(result);
    }

}
