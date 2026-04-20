package fn10.bedrockr.rendering;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

public class RenderHandler {

    private VkInstance inst;

    public void start() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            String name = "bedrockR Rendering";
            final VkApplicationInfo info = VkApplicationInfo.create();
            info.pApplicationName(MemoryUtil.memASCII(name));
            final VkInstanceCreateInfo cInfo = VkInstanceCreateInfo.create();
            cInfo.pApplicationInfo(info);

            PointerBuffer ptr = stack.mallocPointer(1);
            VK14.vkCreateInstance(cInfo, null, ptr);
            inst = new VkInstance(ptr.get(), cInfo);
        }
    }


}
