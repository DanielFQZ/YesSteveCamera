/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.win32.StdCallLibrary
 */
package io.github.tt432.yessteveskill.client.dnd;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

final class WinNative {
    static final int S_OK = 0;
    static final int E_INVALIDARG = -2147024809;
    static final int E_NOINTERFACE = -2147467262;
    static final int CF_HDROP = 15;
    static final int DVASPECT_CONTENT = 1;
    static final int TYMED_HGLOBAL = 1;
    static final int DROPEFFECT_NONE = 0;
    static final int DROPEFFECT_COPY = 1;
    static final int CP_UTF8 = 65001;
    static final byte[] IID_IUNKNOWN = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 70};
    static final byte[] IID_IDROPTARGET = new byte[]{34, 1, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 70};

    private WinNative() {
    }

    static boolean refEquals(Pointer riid, byte[] target) {
        if (riid == null) {
            return false;
        }
        byte[] got = riid.getByteArray(0L, 16);
        if (got.length != target.length) {
            return false;
        }
        for (int i = 0; i < target.length; ++i) {
            if (got[i] == target[i]) continue;
            return false;
        }
        return true;
    }

    public static interface Shell32Lib
    extends StdCallLibrary {
        public static final Shell32Lib INSTANCE = (Shell32Lib)Native.load((String)"shell32", Shell32Lib.class);

        public int DragQueryFileW(Pointer var1, int var2, char[] var3, int var4);
    }

    public static interface Kernel32Lib
    extends StdCallLibrary {
        public static final Kernel32Lib INSTANCE = (Kernel32Lib)Native.load((String)"kernel32", Kernel32Lib.class);

        public Pointer GlobalLock(Pointer var1);

        public boolean GlobalUnlock(Pointer var1);

        public int WideCharToMultiByte(int var1, int var2, char[] var3, int var4, byte[] var5, int var6, Pointer var7, Pointer var8);
    }

    public static interface User32Lib
    extends StdCallLibrary {
        public static final User32Lib INSTANCE = (User32Lib)Native.load((String)"user32", User32Lib.class);

        public boolean ScreenToClient(Pointer var1, Pointer var2);
    }

    public static interface Ole32
    extends StdCallLibrary {
        public static final Ole32 INSTANCE = (Ole32)Native.load((String)"ole32", Ole32.class);

        public int OleInitialize(Pointer var1);

        public int RegisterDragDrop(Pointer var1, Pointer var2);

        public int RevokeDragDrop(Pointer var1);

        public void ReleaseStgMedium(Pointer var1);
    }
}

