package org.photonvision.vision.frame.provider;

import edu.wpi.first.cscore.CvSink;
import org.photonvision.common.util.math.MathUtils;
import org.photonvision.vision.opencv.CVMat;
import org.photonvision.vision.processes.VisionSourceSettables;

public class NetworkFrameProvider extends CpuImageProcessor {
    CvSink cvSink;

    @SuppressWarnings("SpellCheckingInspection")
    private final VisionSourceSettables settables;

    public NetworkFrameProvider(CvSink cvSink, VisionSourceSettables visionSettables) {
        this.cvSink = cvSink;
        this.settables = visionSettables;
    }

    @Override
    public String getName() {
        return "NetworkFrameProvider - " + cvSink.getName();
    }

    @Override
    CapturedFrame getInputMat() {
        var mat = new CVMat(); // We do this so that we don't fill a Mat in use by another thread
        // This is from wpi::Now, or WPIUtilJNI.now()
        long time =
                cvSink.grabFrame(mat.getMat())
                        * 1000; // Units are microseconds, epoch is the same as the Unix epoch

        // Sometimes CSCore gives us a zero frametime.
        if (time <= 1e-6) {
            time = MathUtils.wpiNanoTime();
        }
        var staticFrameProperties = settables.getFrameStaticProperties();

        return new CapturedFrame(mat, staticFrameProperties, time);
    }
}
