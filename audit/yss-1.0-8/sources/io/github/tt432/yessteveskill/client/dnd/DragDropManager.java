/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  org.lwjgl.glfw.GLFWNativeWin32
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.client.dnd;

import com.sun.jna.Pointer;
import io.github.tt432.yessteveskill.client.dnd.PositionedDropTarget;
import io.github.tt432.yessteveskill.client.dnd.WinDropTarget;
import io.github.tt432.yessteveskill.client.dnd.WinNative;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DragDropManager {
    public static final DragDropManager INSTANCE = new DragDropManager();
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/DragDrop");
    private volatile boolean available = false;
    private volatile boolean installed = false;
    private volatile boolean dragging = false;
    private volatile int hoverX = 0;
    private volatile int hoverY = 0;
    private volatile String hoveredFirstPath;
    private volatile Pointer hwnd;
    private volatile Object aliveRef;

    private DragDropManager() {
    }

    public boolean isAvailable() {
        return this.available;
    }

    public boolean isDragging() {
        return this.available && this.dragging;
    }

    public int getHoverX() {
        return this.hoverX;
    }

    public int getHoverY() {
        return this.hoverY;
    }

    Pointer getHwnd() {
        return this.hwnd;
    }

    public String getHoveredFirstPath() {
        return this.hoveredFirstPath;
    }

    private static int toGui(int clientPx) {
        double scale = Minecraft.m_91087_().m_91268_().m_85449_();
        return scale <= 0.0 ? clientPx : (int)((double)clientPx / scale);
    }

    public void install() {
        if (this.installed) {
            return;
        }
        if (!DragDropManager.isWindows()) {
            LOGGER.info("DragDrop hover: \u975e Windows \u5e73\u53f0\uff0c\u8df3\u8fc7 OLE \u6ce8\u518c\uff08\u4f7f\u7528 fallback \u677e\u624b\u5bfc\u5165\uff09");
            this.installed = true;
            return;
        }
        try {
            this.doInstallWindows();
            this.available = true;
            this.installed = true;
            LOGGER.info("DragDrop hover: OLE IDropTarget \u6ce8\u518c\u6210\u529f\uff0chover \u5411\u5bfc\u53ef\u7528");
        }
        catch (Throwable t) {
            LOGGER.warn("DragDrop hover: OLE \u6ce8\u518c\u5931\u8d25\uff0c\u9000\u56de fallback \u677e\u624b\u5bfc\u5165", t);
            this.available = false;
            this.installed = true;
        }
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name", "");
        return os.toLowerCase().contains("win");
    }

    private void doInstallWindows() {
        long glfwHandle = Minecraft.m_91087_().m_91268_().m_85439_();
        long hwndVal = GLFWNativeWin32.glfwGetWin32Window((long)glfwHandle);
        if (hwndVal == 0L) {
            throw new IllegalStateException("glfwGetWin32Window \u8fd4\u56de 0");
        }
        this.hwnd = new Pointer(hwndVal);
        int hr = WinNative.Ole32.INSTANCE.OleInitialize(null);
        LOGGER.debug("OleInitialize hr=0x{}", (Object)Integer.toHexString(hr));
        int hrRevoke = WinNative.Ole32.INSTANCE.RevokeDragDrop(this.hwnd);
        LOGGER.debug("RevokeDragDrop(GLFW) hr=0x{}", (Object)Integer.toHexString(hrRevoke));
        WinDropTarget target = new WinDropTarget(this);
        int hrReg = WinNative.Ole32.INSTANCE.RegisterDragDrop(this.hwnd, target.handle());
        if (hrReg != 0) {
            throw new IllegalStateException("RegisterDragDrop \u5931\u8d25 hr=0x" + Integer.toHexString(hrReg));
        }
        this.keepAlive(target);
    }

    void onDragEnter(int clientX, int clientY, String firstPath) {
        this.dragging = true;
        this.hoverX = DragDropManager.toGui(clientX);
        this.hoverY = DragDropManager.toGui(clientY);
        this.hoveredFirstPath = firstPath;
        LOGGER.info("DragEnter gui=({}, {}) first={}", new Object[]{this.hoverX, this.hoverY, firstPath});
    }

    void onDragOver(int clientX, int clientY) {
        this.hoverX = DragDropManager.toGui(clientX);
        this.hoverY = DragDropManager.toGui(clientY);
    }

    void onDragLeave() {
        this.dragging = false;
        LOGGER.info("DragLeave");
    }

    void onDrop(int clientX, int clientY, List<String> paths) {
        this.dragging = false;
        int gx = DragDropManager.toGui(clientX);
        int gy = DragDropManager.toGui(clientY);
        LOGGER.info("Drop gui=({}, {}) paths={}", new Object[]{gx, gy, paths});
        this.dispatchDrop(gx, gy, paths);
    }

    private void dispatchDrop(int x, int y, List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen == null) {
            return;
        }
        ArrayList<Path> pathList = new ArrayList<Path>();
        for (String p : paths) {
            if (p == null || p.isEmpty()) continue;
            pathList.add(Paths.get(p, new String[0]));
        }
        if (pathList.isEmpty()) {
            return;
        }
        if (screen instanceof PositionedDropTarget) {
            PositionedDropTarget pdt = (PositionedDropTarget)screen;
            mc.execute(() -> pdt.onFilesDropWithPosition(pathList, x, y));
        } else {
            mc.execute(() -> screen.m_7400_(pathList));
        }
    }

    private void keepAlive(Object target) {
        this.aliveRef = target;
    }
}

