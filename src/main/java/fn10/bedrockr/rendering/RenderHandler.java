package fn10.bedrockr.rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class RenderHandler {

    private VkInstance inst;

    static void main() {
        new RenderHandler().start();
    }

    public void start() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // create inst
            String name = "bedrockR Rendering";
            final VkApplicationInfo info = VkApplicationInfo.create();
            info.pApplicationName(MemoryUtil.memASCII(name));
            final VkInstanceCreateInfo cInfo = VkInstanceCreateInfo.create();
            cInfo.pApplicationInfo(info);

            PointerBuffer ptr = stack.mallocPointer(1);
            VK14.vkCreateInstance(cInfo, null, ptr);
            inst = new VkInstance(ptr.get(), cInfo);

            // pick device
            PointerBuffer devicePtr = stack.mallocPointer(16);
            final int[] devices = new int[16];
            VK10.vkEnumeratePhysicalDevices(inst, devices, devicePtr);
            ArrayList<VkPhysicalDevice> devicesList = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                final long deviceHandle = devicePtr.get(i);
                if (deviceHandle == 0) continue;
                devicesList.add(new VkPhysicalDevice(deviceHandle, inst));
            }
return;
        } catch (Exception e) {
            throw new RenderingException("Failed to start Vulkan Renderer", e);
        }
    }

    public static class RenderingException extends RuntimeException {
        public RenderingException(String message) {
            super(message);
        }
        public RenderingException(String message, Throwable ex) {
            super(message, ex);
        }
    }
}
