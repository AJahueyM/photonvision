package org.photonvision.vision.camera;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.*;
import edu.wpi.first.util.PixelFormat;
import java.util.HashMap;
import java.util.Objects;
import org.photonvision.common.configuration.CameraConfiguration;
import org.photonvision.vision.frame.FrameProvider;
import org.photonvision.vision.frame.provider.NetworkFrameProvider;
import org.photonvision.vision.processes.VisionSource;
import org.photonvision.vision.processes.VisionSourceSettables;

public class NetworkCameraSource extends VisionSource {
    private final FrameProvider networkFrameProvider;
    private final NetworkCameraSettables networkCameraSettables;
    private final HttpCamera httpCamera;
    private final CvSink cvSink;

    public NetworkCameraSource(CameraConfiguration cameraConfiguration) {
        super(cameraConfiguration);

        httpCamera = new HttpCamera(cameraConfiguration.nickname, cameraConfiguration.path);
        cvSink = CameraServer.getVideo(httpCamera);
        networkCameraSettables = new NetworkCameraSettables(cameraConfiguration);
        networkFrameProvider = new NetworkFrameProvider(cvSink, networkCameraSettables);
    }

    public class NetworkCameraSettables extends VisionSourceSettables {
        private final VideoMode videoMode;

        protected NetworkCameraSettables(CameraConfiguration configuration) {
            super(configuration);
            videoMode = new VideoMode(PixelFormat.kMJPEG, 1280, 800, 120);

            videoModes = new HashMap<>();
            videoModes.put(0, videoMode);
        }

        @Override
        public void setExposure(double exposure) {
            httpCamera.setExposureManual((int) exposure);
        }

        @Override
        public void setAutoExposure(boolean cameraAutoExposure) {
            if (cameraAutoExposure) {
                httpCamera.setExposureAuto();
            } else {
                httpCamera.setExposureHoldCurrent();
            }
        }

        @Override
        public void setBrightness(int brightness) {
            httpCamera.setBrightness(brightness);
        }

        @Override
        public void setGain(int gain) {}

        @Override
        public VideoMode getCurrentVideoMode() {
            return videoMode;
        }

        @Override
        public HashMap<Integer, VideoMode> getAllVideoModes() {
            return videoModes;
        }

        @Override
        public void setVideoModeInternal(VideoMode videoMode) {
            try {
                if (videoMode == null) {
                    logger.error("Got a null video mode! Doing nothing...");
                }
            } catch (Exception e) {
                logger.error("Failed to set video mode!", e);
            }
        }
    }

    @Override
    public FrameProvider getFrameProvider() {
        return networkFrameProvider;
    }

    @Override
    public VisionSourceSettables getSettables() {
        return networkCameraSettables;
    }

    @Override
    public boolean isVendorCamera() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        NetworkCameraSource other = (NetworkCameraSource) obj;

        if (httpCamera == null) {
            if (other.httpCamera != null) return false;
        } else if (!httpCamera.equals(other.httpCamera)) return false;
        if (networkCameraSettables == null) {
            if (other.networkCameraSettables != null) return false;
        } else if (!networkCameraSettables.equals(other.networkCameraSettables)) return false;
        if (networkFrameProvider == null) {
            if (other.networkFrameProvider != null) return false;
        } else if (!networkFrameProvider.equals(other.networkFrameProvider)) return false;
        if (cvSink == null) {
            if (other.cvSink != null) return false;
        } else if (!cvSink.equals(other.cvSink)) return false;
        if (getCameraConfiguration().cameraQuirks == null) {
            if (other.getCameraConfiguration().cameraQuirks != null) return false;
        } else if (!getCameraConfiguration()
                .cameraQuirks
                .equals(other.getCameraConfiguration().cameraQuirks)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                httpCamera,
                networkCameraSettables,
                networkFrameProvider,
                cameraConfiguration,
                cvSink,
                getCameraConfiguration().cameraQuirks);
    }
}
