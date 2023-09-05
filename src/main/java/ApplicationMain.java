import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.opencv.core.Core;

@QuarkusMain
public class ApplicationMain {

    public static void main(String ... args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Quarkus.run(args);
    }

}