/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Callback
 *  com.sun.jna.CallbackReference
 *  com.sun.jna.Function
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$ByValue
 *  com.sun.jna.Structure$FieldOrder
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.PointerByReference
 *  com.sun.jna.win32.StdCallLibrary$StdCallCallback
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.client.dnd;

import com.sun.jna.Callback;
import com.sun.jna.CallbackReference;
import com.sun.jna.Function;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import io.github.tt432.yessteveskill.client.dnd.DragDropManager;
import io.github.tt432.yessteveskill.client.dnd.WinNative;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class WinDropTarget {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/DragDrop");
    private final int ps = Native.POINTER_SIZE;
    private final Memory vtable = new Memory((long)this.ps * 7L);
    private final Memory object = new Memory((long)this.ps);
    private final QueryInterfaceCb qi = (self, riid, ppv) -> {
        if (ppv == null) {
            return -2147024809;
        }
        if (WinNative.refEquals(riid, WinNative.IID_IUNKNOWN) || WinNative.refEquals(riid, WinNative.IID_IDROPTARGET)) {
            ppv.setValue(self);
            return 0;
        }
        ppv.setValue(null);
        return -2147467262;
    };
    private final AddRefCb addRef = self -> 1;
    private final ReleaseCb release = self -> 1;
    private final DragEnterCb dragEnter = (self, dataObj, keyState, pt, pdwEffect) -> {
        if (pdwEffect == null) {
            return -2147024809;
        }
        if (!this.queryHasHdrop(dataObj)) {
            pdwEffect.setValue(0);
            return 0;
        }
        int[] xy = this.screenToClient(pt.x, pt.y);
        List<String> paths = this.extractPaths(dataObj);
        String first = paths.isEmpty() ? null : paths.get(0);
        manager.onDragEnter(xy[0], xy[1], first);
        pdwEffect.setValue(1);
        return 0;
    };
    private final DragOverCb dragOver = (self, keyState, pt, pdwEffect) -> {
        if (pdwEffect == null) {
            return -2147024809;
        }
        int[] xy = this.screenToClient(pt.x, pt.y);
        manager.onDragOver(xy[0], xy[1]);
        pdwEffect.setValue(1);
        return 0;
    };
    private final DragLeaveCb dragLeave = self -> {
        manager.onDragLeave();
        return 0;
    };
    private final DropCb drop = (self, dataObj, keyState, pt, pdwEffect) -> {
        int[] xy = this.screenToClient(pt.x, pt.y);
        List<String> paths = this.extractPaths(dataObj);
        manager.onDrop(xy[0], xy[1], paths);
        if (pdwEffect != null) {
            pdwEffect.setValue(paths.isEmpty() ? 0 : 1);
        }
        return 0;
    };

    WinDropTarget(DragDropManager manager) {
        this.vtable.setPointer(0L, CallbackReference.getFunctionPointer((Callback)this.qi));
        this.vtable.setPointer((long)this.ps, CallbackReference.getFunctionPointer((Callback)this.addRef));
        this.vtable.setPointer(2L * (long)this.ps, CallbackReference.getFunctionPointer((Callback)this.release));
        this.vtable.setPointer(3L * (long)this.ps, CallbackReference.getFunctionPointer((Callback)this.dragEnter));
        this.vtable.setPointer(4L * (long)this.ps, CallbackReference.getFunctionPointer((Callback)this.dragOver));
        this.vtable.setPointer(5L * (long)this.ps, CallbackReference.getFunctionPointer((Callback)this.dragLeave));
        this.vtable.setPointer(6L * (long)this.ps, CallbackReference.getFunctionPointer((Callback)this.drop));
        this.object.setPointer(0L, (Pointer)this.vtable);
    }

    Pointer handle() {
        return this.object;
    }

    private int[] screenToClient(int sx, int sy) {
        Memory p = new Memory(8L);
        p.setInt(0L, sx);
        p.setInt(4L, sy);
        try {
            WinNative.User32Lib.INSTANCE.ScreenToClient(DragDropManager.INSTANCE.getHwnd(), (Pointer)p);
        }
        catch (Throwable t) {
            LOGGER.warn("ScreenToClient failed", t);
        }
        return new int[]{p.getInt(0L), p.getInt(4L)};
    }

    private boolean queryHasHdrop(Pointer dataObj) {
        if (dataObj == null) {
            return false;
        }
        try {
            Memory fmt = this.formatetcHdrop();
            Pointer vtbl = dataObj.getPointer(0L);
            Pointer fn = vtbl.getPointer(5L * (long)this.ps);
            Function f = Function.getFunction((Pointer)fn, (int)63);
            int hr = f.invokeInt(new Object[]{dataObj, fmt});
            LOGGER.debug("QueryGetData hr=0x{}", (Object)Integer.toHexString(hr));
            return hr == 0;
        }
        catch (Throwable t) {
            LOGGER.warn("QueryGetData failed", t);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private List<String> extractPaths(Pointer dataObj) {
        Pointer hDrop;
        Pointer hGlobal;
        Memory medium;
        block26: {
            block25: {
                block24: {
                    if (dataObj == null) {
                        return List.of();
                    }
                    Memory fmt = this.formatetcHdrop();
                    medium = new Memory(24L);
                    Pointer vtbl = dataObj.getPointer(0L);
                    Pointer fn = vtbl.getPointer(3L * (long)this.ps);
                    Function f = Function.getFunction((Pointer)fn, (int)63);
                    int hr = f.invokeInt(new Object[]{dataObj, fmt, medium});
                    if (hr == 0) break block24;
                    LOGGER.debug("GetData returned hr=0x{}", (Object)Integer.toHexString(hr));
                    List list = List.of();
                    try {
                        WinNative.Ole32.INSTANCE.ReleaseStgMedium((Pointer)medium);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                    return list;
                }
                hGlobal = medium.getPointer(8L);
                if (hGlobal != null) break block25;
                List list = List.of();
                try {
                    WinNative.Ole32.INSTANCE.ReleaseStgMedium((Pointer)medium);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                return list;
            }
            hDrop = WinNative.Kernel32Lib.INSTANCE.GlobalLock(hGlobal);
            if (hDrop != null) break block26;
            List list = List.of();
            try {
                WinNative.Ole32.INSTANCE.ReleaseStgMedium((Pointer)medium);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return list;
        }
        List<String> list = this.readHdropPaths(hDrop);
        WinNative.Kernel32Lib.INSTANCE.GlobalUnlock(hGlobal);
        try {
            WinNative.Ole32.INSTANCE.ReleaseStgMedium((Pointer)medium);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return list;
        {
            catch (Throwable throwable) {
                try {
                    try {
                        WinNative.Kernel32Lib.INSTANCE.GlobalUnlock(hGlobal);
                        throw throwable;
                    }
                    catch (Throwable t) {
                        LOGGER.warn("extractPaths failed", t);
                        List list2 = List.of();
                        return list2;
                    }
                }
                catch (Throwable throwable2) {
                    throw throwable2;
                }
                finally {
                    try {
                        WinNative.Ole32.INSTANCE.ReleaseStgMedium((Pointer)medium);
                    }
                    catch (Throwable throwable3) {}
                }
            }
        }
    }

    private List<String> readHdropPaths(Pointer hDrop) {
        ArrayList<String> paths = new ArrayList<String>();
        int count = WinNative.Shell32Lib.INSTANCE.DragQueryFileW(hDrop, -1, null, 0);
        for (int i = 0; i < count; ++i) {
            int len = WinNative.Shell32Lib.INSTANCE.DragQueryFileW(hDrop, i, null, 0);
            if (len <= 0) continue;
            char[] wbuf = new char[len + 1];
            WinNative.Shell32Lib.INSTANCE.DragQueryFileW(hDrop, i, wbuf, wbuf.length);
            int utf8Len = WinNative.Kernel32Lib.INSTANCE.WideCharToMultiByte(65001, 0, wbuf, len, null, 0, null, null);
            if (utf8Len <= 0) continue;
            byte[] out = new byte[utf8Len];
            WinNative.Kernel32Lib.INSTANCE.WideCharToMultiByte(65001, 0, wbuf, len, out, out.length, null, null);
            paths.add(new String(out, 0, utf8Len, StandardCharsets.UTF_8));
        }
        return paths;
    }

    private Memory formatetcHdrop() {
        Memory fmt = new Memory(32L);
        fmt.clear();
        fmt.setShort(0L, (short)15);
        fmt.setInt(16L, 1);
        fmt.setInt(20L, -1);
        fmt.setInt(24L, 1);
        return fmt;
    }

    @FunctionalInterface
    static interface QueryInterfaceCb
    extends StdCallLibrary.StdCallCallback {
        public int invoke(Pointer var1, Pointer var2, PointerByReference var3);
    }

    @FunctionalInterface
    static interface AddRefCb
    extends StdCallLibrary.StdCallCallback {
        public int invoke(Pointer var1);
    }

    @FunctionalInterface
    static interface ReleaseCb
    extends StdCallLibrary.StdCallCallback {
        public int invoke(Pointer var1);
    }

    @FunctionalInterface
    static interface DragEnterCb
    extends StdCallLibrary.StdCallCallback {
        public int invoke(Pointer var1, Pointer var2, int var3, POINTL var4, IntByReference var5);
    }

    @FunctionalInterface
    static interface DragOverCb
    extends StdCallLibrary.StdCallCallback {
        public int invoke(Pointer var1, int var2, POINTL var3, IntByReference var4);
    }

    @FunctionalInterface
    static interface DragLeaveCb
    extends StdCallLibrary.StdCallCallback {
        public int invoke(Pointer var1);
    }

    @FunctionalInterface
    static interface DropCb
    extends StdCallLibrary.StdCallCallback {
        public int invoke(Pointer var1, Pointer var2, int var3, POINTL var4, IntByReference var5);
    }

    @Structure.FieldOrder(value={"x", "y"})
    public static class POINTL
    extends Structure
    implements Structure.ByValue {
        public int x;
        public int y;
    }
}

