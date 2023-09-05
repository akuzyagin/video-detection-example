# video-detection-example

You should run `target/quarkus-app/quarkus-run.jar` after `clean` and `install`.
Same if you run it via IDE.

[yolov5x.torchscript.zip](model%2Fyolov5x.torchscript.zip) - pretrained `yolov5x` from https://github.com/ultralytics/yolov5#pretrained-checkpoints converted via `export.py` (https://github.com/ultralytics/yolov5/blob/master/export.py) to torchscript and zipped by linux zip util

[libopencv_java470.so](3rdparty%2Flibopencv_java470.so) - OpenCV lib build from src (https://github.com/opencv/opencv)

# Requirements: 
* `Nvidia`
* `CUDA`
* `CUDNN`
* `ffmpeg`
* `OpenCV (4.7.0)`
* `GraalVM (17.0.7)`